package com.lumin.site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for constructing and delivering outbound email notifications
 * whenever a new laboratory consultation enquiry is received.
 * <p>
 * Transmits structured plain-text digests to the consultancy inbox via SMTP/TLS,
 * setting the client's email as the {@code Reply-To} address to facilitate 1-click
 * executive follow-ups. Includes protection against SMTP header injection.
 * </p>
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    /**
     * Spring Mail abstraction for sending MIME and simple mail messages.
     */
    private final JavaMailSender sender;

    /**
     * Recipient email address where consultation notifications are delivered (e.g., ceo.lumin@gmail.com).
     */
    private final String to;

    /**
     * Authenticated sender account used for outbound SMTP relay.
     */
    private final String from;

    /**
     * Constructs the MailService with required mail transport dependencies and configuration values.
     *
     * @param sender Spring's configured {@link JavaMailSender}.
     * @param to     Target inbox address injected from {@code lumin.enquiry.to}.
     * @param from   Originating SMTP username injected from {@code spring.mail.username}.
     */
    public MailService(JavaMailSender sender,
                       @Value("${lumin.enquiry.to}") String to,
                       @Value("${spring.mail.username}") String from) {
        this.sender = sender;
        this.to = to;
        this.from = from;
    }

    /**
     * Returns the primary recipient email address configured for enquiry notifications.
     *
     * @return The target inbox email address string.
     */
    public String recipient() { return to; }

    /**
     * Formats and dispatches an email notification summarizing the client consultation request.
     * <p>
     * If SMTP authentication is unconfigured, logs a warning and gracefully skips dispatch
     * to avoid interrupting application workflow.
     * </p>
     *
     * @param r The validated {@link EnquiryRequest} submitted by the client.
     */
    public void sendEnquiry(EnquiryRequest r) {
        if (from == null || from.isBlank()) {
            log.warn("SMTP username not configured. Skipping email notification for enquiry from: {}", r.email());
            return;
        }
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(from);
        m.setTo(to);
        m.setReplyTo(r.email());
        m.setSubject("New website enquiry from " + clean(r.name()) + " (" + clean(r.organization()) + ")");
        m.setText("""
                New enquiry from the Lumin Consultancy website

                Name:                 %s
                Organization:         %s
                Designation:          %s
                Email:                %s
                Phone:                %s
                Laboratory sector:    %s
                Service required:     %s
                Accreditation status: %s
                Preferred contact:    %s

                Message:
                %s
                """.formatted(r.name(), r.organization(), nz(r.designation()), r.email(), r.phone(),
                nz(r.sector()), nz(r.service()), nz(r.status()), nz(r.preferred()), nz(r.message())));
        sender.send(m);
    }

    /**
     * Helper to return a default placeholder if the supplied string is null or blank.
     *
     * @param s Inbound string candidate.
     * @return The trimmed original string or {@code "-"} if empty.
     */
    private static String nz(String s) { return s == null || s.isBlank() ? "-" : s; }

    /** Strip line breaks so visitor input can never inject mail headers. */
    private static String clean(String s) { return s.replaceAll("[\\r\\n]+", " "); }
}
