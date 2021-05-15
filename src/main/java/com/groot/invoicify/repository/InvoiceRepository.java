package com.groot.invoicify.repository;

import com.groot.invoicify.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Repository
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {
//    List<Invoice> findByCompanyCompanyId(Long compId);
    List<Invoice> findByCompanyCompanyId(Long compId,Pageable pageable);
    List<Invoice> findByCompanyCompanyIdAndPaid(Long compId, boolean b);

    Invoice findByInvoiceId(Long invoiceNum);
}
