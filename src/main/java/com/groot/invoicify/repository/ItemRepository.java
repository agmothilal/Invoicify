package com.groot.invoicify.repository;

import com.groot.invoicify.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ItemRepository
 *
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	/**
	 *
	 * @param invoiceId
	 * @return
	 */
	List<Item> findByInvoiceInvoiceId(Long invoiceId);
}
