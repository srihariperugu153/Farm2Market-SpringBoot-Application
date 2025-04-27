package com.farmtomarket.application.repository;

import com.farmtomarket.application.model.Products;
import com.farmtomarket.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Products,Integer> {
    Page<Products> findByCityContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String city, Pageable pageable, double quantity);

    Page<Products> findByCityContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String city, String searchString, Pageable pageable, double quantity);

    @Query("SELECT p FROM Products p WHERE p.quantity > 0 AND p.deleteFlag = false ORDER BY CASE WHEN p.city = :city THEN 0 ELSE 1 END, p.city")
    Page<Products> findAllOrderByCityFirst(@Param("city") String city, Pageable pageable);

    Page<Products> findByProductNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String searchString, Pageable pageable, double quantity);

    List<Products> findByFarmerAndDeleteFlag(User user, boolean inDraft);
}
