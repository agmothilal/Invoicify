package com.groot.invoicify.repository;

import com.groot.invoicify.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByInvoiceInvoiceId(Long invoiceId);
}
