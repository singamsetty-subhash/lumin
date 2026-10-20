package com.lumin.site;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA Entity representing a consultation enquiry submitted by a laboratory client.
 * <p>
 * Maps to the {@code enquiries} relational table in MySQL, storing client identity,
 * organization metadata, sector of interest, requested consulting service,
 * current accreditation status, and custom requirement message.
 * </p>
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
@Entity
@Table(name = "enquiries")
public class Enquiry {

    /**
     * Unique surrogate primary key, automatically generated using MySQL auto-increment strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the prospect / laboratory representative (required, max 120 characters).
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Name of the client company or testing facility (required, max 160 characters).
     */
    @Column(nullable = false, length = 160)
    private String organization;

    /**
     * Professional designation / title of the applicant (e.g., Quality Manager, Director).
     */
    @Column(length = 120)
    private String designation;

    /**
     * Direct contact email address of the applicant (required, max 160 characters).
     */
    @Column(nullable = false, length = 160)
    private String email;

    /**
     * Primary contact phone or mobile number (required, max 40 characters).
     */
    @Column(nullable = false, length = 40)
    private String phone;

    /**
     * Testing sector or industry domain (e.g., Food & Agriculture, Environmental, Pharmaceutical).
     */
    @Column(length = 120)
    private String sector;

    /**
     * Scope of consulting service required (e.g., NABL Accreditation, ISO 17025, Method Validation).
     */
    @Column(length = 120)
    private String service;

    /**
     * Current accreditation readiness or certification stage of the client laboratory.
     */
    @Column(length = 120)
    private String status;

    /**
     * Detailed requirement description or scope inquiry submitted by the client (up to 3000 chars).
     */
    @Column(length = 3000)
    private String message;

    /**
     * Preferred channel for follow-up communication (e.g., Email, Phone, WhatsApp).
     */
    @Column(length = 40)
    private String preferred;

    /**
     * UTC/IST timestamp marking when the enquiry record was created.
     */
    private Instant createdAt;

    /**
     * Lifecycle callback invoked before entity insertion to populate {@link #createdAt} automatically.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    /**
     * Default no-argument constructor required by JPA specification.
     */
    public Enquiry() {}

    /**
     * @return The unique primary key ID of the enquiry.
     */
    public Long getId() { return id; }

    /**
     * @param id The unique primary key ID to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return The client's full name.
     */
    public String getName() { return name; }

    /**
     * @param name The client's full name to set.
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return The client laboratory or company name.
     */
    public String getOrganization() { return organization; }

    /**
     * @param organization The organization name to set.
     */
    public void setOrganization(String organization) { this.organization = organization; }

    /**
     * @return The client's professional designation.
     */
    public String getDesignation() { return designation; }

    /**
     * @param designation The professional designation to set.
     */
    public void setDesignation(String designation) { this.designation = designation; }

    /**
     * @return The client's contact email address.
     */
    public String getEmail() { return email; }

    /**
     * @param email The contact email address to set.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return The client's phone number.
     */
    public String getPhone() { return phone; }

    /**
     * @param phone The contact phone number to set.
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * @return The laboratory industry sector.
     */
    public String getSector() { return sector; }

    /**
     * @param sector The laboratory industry sector to set.
     */
    public void setSector(String sector) { this.sector = sector; }

    /**
     * @return The consulting service requested.
     */
    public String getService() { return service; }

    /**
     * @param service The consulting service to set.
     */
    public void setService(String service) { this.service = service; }

    /**
     * @return The current accreditation stage.
     */
    public String getStatus() { return status; }

    /**
     * @param status The accreditation stage to set.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * @return The detailed message or requirements.
     */
    public String getMessage() { return message; }

    /**
     * @param message The detailed message to set.
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * @return The preferred contact method (Email, Phone, WhatsApp).
     */
    public String getPreferred() { return preferred; }

    /**
     * @param preferred The preferred contact method to set.
     */
    public void setPreferred(String preferred) { this.preferred = preferred; }

    /**
     * @return The timestamp when this enquiry was persisted.
     */
    public Instant getCreatedAt() { return createdAt; }

    /**
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
