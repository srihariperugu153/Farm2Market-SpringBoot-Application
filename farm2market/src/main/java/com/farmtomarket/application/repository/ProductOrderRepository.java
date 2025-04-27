package com.farmtomarket.application.repository;

import com.farmtomarket.application.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {
    Page<ProductOrder> findByTraderId(long id, Pageable pageable);
}
