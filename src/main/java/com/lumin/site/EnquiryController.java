package com.lumin.site;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.Sort;

/**
 * REST Controller exposing public and administrative API endpoints for consultation enquiry handling.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Receiving and validating consultation requests ({@code POST /api/enquiry})</li>
 *   <li>Anti-spam bot filtering via honeypot field inspection</li>
 *   <li>Sliding-window IP rate limiting (maximum 5 submissions per hour per IP)</li>
 *   <li>Persisting enquiries to the database and safely queuing email alerts</li>
 *   <li>Serving sorted enquiry records for the administrative dashboard ({@code GET /api/enquiries})</li>
 *   <li>Handling single-record deletions ({@code DELETE /api/enquiries/{id}})</li>
 * </ul>
 * </p>
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
public class EnquiryController {

    private static final Logger log = LoggerFactory.getLogger(EnquiryController.class);

    /** Maximum allowed enquiry submissions per IP address within a 1-hour window. */
    private static final int MAX_PER_HOUR = 5;

    /** Thread-safe map tracking remote client IP addresses to submission timestamps. */
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    private final MailService mail;
    private final EnquiryRepository repository;

    /**
     * Injects the required {@link MailService} and {@link EnquiryRepository} dependencies.
     *
     * @param mail       The notification mail service.
     * @param repository The enquiry database repository.
     */
    public EnquiryController(MailService mail, EnquiryRepository repository) {
        this.mail = mail;
        this.repository = repository;
    }

    /**
     * Exposes the primary recipient email configured for the consultancy.
     *
     * @return A map containing the recipient email address.
     */
    @GetMapping("/contact")
    public Map<String, String> contact() {
        return Map.of("email", mail.recipient());
    }

    /**
     * Retrieves all recorded consultation enquiries, sorted in descending order of creation date.
     *
     * @return A {@link ResponseEntity} containing the list of all {@link Enquiry} entities.
     */
    @GetMapping("/enquiries")
    public ResponseEntity<List<Enquiry>> getEnquiries() {
        return ResponseEntity.ok(repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    /**
     * Permanently deletes a single enquiry record from the database by its primary key ID.
     *
     * @param id The unique identifier of the enquiry to delete.
     * @return HTTP 200 with success status if found and deleted, or HTTP 404 if not found.
     */
    @DeleteMapping("/enquiries/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnquiry(@PathVariable long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Enquiry deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Enquiry not found"));
        }
    }

    /**
     * Processes incoming consultation enquiry submissions from the public website form.
     * <p>
     * Implements a honeypot trap to silently discard bot submissions, enforces sliding-window
     * rate limiting per IP, persists the entity to MySQL, and attempts asynchronous notification
     * dispatch without compromising database transaction integrity.
     * </p>
     *
     * @param req  The validated {@link EnquiryRequest} payload.
     * @param http The servlet request used to determine the client's remote IP address.
     * @return HTTP 200 on success, HTTP 429 on rate limit breach, or HTTP 500 on database failure.
     */
    @PostMapping("/enquiry")
    public ResponseEntity<Map<String, Object>> submit(@Valid @RequestBody EnquiryRequest req,
            HttpServletRequest http) {
        // Honeypot filled in => bot. Pretend success, send nothing.
        if (req.website() != null && !req.website().isBlank()) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        if (tooMany(http.getRemoteAddr())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("success", false, "error", "Too many enquiries. Please try again later."));
        }
        Enquiry enquiry;
        try {
            enquiry = new Enquiry();
            enquiry.setName(req.name());
            enquiry.setOrganization(req.organization());
            enquiry.setDesignation(req.designation());
            enquiry.setEmail(req.email());
            enquiry.setPhone(req.phone());
            enquiry.setSector(req.sector());
            enquiry.setService(req.service());
            enquiry.setStatus(req.status());
            enquiry.setMessage(req.message());
            enquiry.setPreferred(req.preferred());
            enquiry = repository.save(enquiry);
            log.info("Enquiry successfully recorded in database with id: {}", enquiry.getId());
        } catch (Exception e) {
            log.error("Failed to save enquiry to database", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error",
                            "We could not save your enquiry right now. Please try again shortly."));
        }

        // Send email notification in a safe try-catch so email provider/SMTP issues never fail a saved enquiry
        try {
            mail.sendEnquiry(req);
        } catch (Exception e) {
            log.warn("Enquiry #{} saved to database, but email delivery failed: {}", enquiry.getId(), e.getMessage());
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Centralized exception handler for validation constraint violations on inbound DTO payloads.
     *
     * @param e The thrown {@link MethodArgumentNotValidException}.
     * @return HTTP 400 Bad Request with a user-friendly error message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Please complete all required fields with valid details."));
    }

    /**
     * Thread-safe sliding-window rate limiter checking if a client IP has exceeded the hourly quota.
     *
     * @param ip The remote client IP address.
     * @return {@code true} if the request should be blocked, {@code false} if within quota.
     */
    private boolean tooMany(String ip) {
        long now = System.currentTimeMillis(), cutoff = now - 3_600_000L;
        Deque<Long> q = hits.computeIfAbsent(ip, k -> new ArrayDeque<>());
        synchronized (q) {
            while (!q.isEmpty() && q.peekFirst() < cutoff)
                q.pollFirst();
            if (q.size() >= MAX_PER_HOUR)
                return true;
            q.addLast(now);
            return false;
        }
    }
}
