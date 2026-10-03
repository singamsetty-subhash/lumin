package com.lumin.site;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
