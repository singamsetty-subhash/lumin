package com.lumin.site;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender sender;
    private final String to;
    private final String from;

    public MailService(JavaMailSender sender,
                       @Value("${lumin.enquiry.to}") String to,
                       @Value("${spring.mail.username}") String from) {
        this.sender = sender;
        this.to = to;
        this.from = from;
    }

    public String recipient() { return to; }

    public void sendEnquiry(EnquiryRequest r) {
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

    private static String nz(String s) { return s == null || s.isBlank() ? "-" : s; }

    /** Strip line breaks so visitor input can never inject mail headers. */
    private static String clean(String s) { return s.replaceAll("[\\r\\n]+", " "); }
}
