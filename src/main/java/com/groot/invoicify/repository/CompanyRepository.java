package com.groot.invoicify.repository;

import com.groot.invoicify.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CompanyRepository
 *
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

	/**
	 *
	 * @param companyName
	 * @return
	 */
	Company findByName(String companyName);

}
