package com.groot.invoicify.repository;

import com.groot.invoicify.entity.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * InvoiceRepository
 *
 */
@Repository
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

	/**
	 *
	 * @param compId
	 * @param pageable
	 * @return
	 */
	List<Invoice> findByCompanyCompanyId(Long compId, Pageable pageable);

	/**
	 *
	 * @param compId
	 * @param b
	 * @param pageable
	 * @return
	 */
	List<Invoice> findByCompanyCompanyIdAndPaid(Long compId, boolean b, Pageable pageable);

	/**
	 *
	 * @param invoiceNum
	 * @return
	 */
	Invoice findByInvoiceId(Long invoiceNum);
}
