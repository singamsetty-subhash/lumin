package com.lumin.site;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository interface for {@link Enquiry} entities.
 * <p>
 * Provides full CRUD operations, pagination, and sorting capabilities against
 * the {@code enquiries} MySQL table without requiring boilerplate SQL.
 * </p>
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
}
