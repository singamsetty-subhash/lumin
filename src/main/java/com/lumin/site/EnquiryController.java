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

@RestController
@RequestMapping("/api")
public class EnquiryController {

    private static final Logger log = LoggerFactory.getLogger(EnquiryController.class);
    private static final int MAX_PER_HOUR = 5;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();
    private final MailService mail;
    private final EnquiryRepository repository;

    public EnquiryController(MailService mail, EnquiryRepository repository) {
        this.mail = mail;
        this.repository = repository;
    }

    @GetMapping("/contact")
    public Map<String, String> contact() {
        return Map.of("email", mail.recipient());
    }

    @GetMapping("/enquiries")
    public ResponseEntity<List<Enquiry>> getEnquiries() {
        return ResponseEntity.ok(repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @DeleteMapping("/enquiries/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnquiry(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Enquiry deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Enquiry not found"));
        }
    }

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
        try {
            Enquiry enquiry = new Enquiry();
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
            repository.save(enquiry);

            mail.sendEnquiry(req);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("Enquiry email failed", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("success", false, "error",
                            "We could not send your enquiry right now. our Team will Contact you "));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Please complete all required fields with valid details."));
    }

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
