package com.lumin.site;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Immutable Data Transfer Object (DTO) capturing client consultation submissions.
 * <p>
 * Enforces declarative Bean Validation on inbound JSON payloads from the public
 * consultation form, ensuring all mandatory contact fields meet length and format
 * requirements before database persistence.
 * </p>
 *
 * @param name         Full name of the prospect/representative (mandatory, max 120 characters).
 * @param organization Laboratory or company name (mandatory, max 160 characters).
 * @param designation  Applicant job title or role (optional, max 120 characters).
 * @param email        Contact email address (mandatory, RFC compliant, max 160 characters).
 * @param phone        Contact phone number (mandatory, max 40 characters).
 * @param sector       Testing sector or industry area (optional, max 120 characters).
 * @param service      Requested consulting or training service (optional, max 120 characters).
 * @param status       Current accreditation readiness or certification stage (optional, max 120 characters).
 * @param message      Detailed inquiry message or scope requirements (optional, max 3000 characters).
 * @param preferred    Preferred follow-up communication method (optional, max 40 characters).
 * @param website      Anti-spam honeypot field; genuine web visitors leave this blank, bots populate it.
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
public record EnquiryRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 160) String organization,
        @Size(max = 120) String designation,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 40) String phone,
        @Size(max = 120) String sector,
        @Size(max = 120) String service,
        @Size(max = 120) String status,
        @Size(max = 3000) String message,
        @Size(max = 40) String preferred,
        String website // honeypot: real visitors leave this empty
) {}
