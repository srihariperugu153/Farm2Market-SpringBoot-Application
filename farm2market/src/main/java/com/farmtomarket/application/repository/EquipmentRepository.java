package com.farmtomarket.application.repository;

import com.farmtomarket.application.model.Equipments;
import com.farmtomarket.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface EquipmentRepository extends JpaRepository<Equipments,Integer> {
    Page<Equipments> findByCityContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String city, Pageable pageable, double quantity);

    Page<Equipments> findByCityContainingIgnoreCaseAndEquipNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String city,String searchString, Pageable pageable, double quantity);

    @Query("SELECT e FROM Equipments e WHERE e.quantity > 0 AND e.deleteFlag = false ORDER BY CASE WHEN e.city = :city  THEN 0 ELSE 1 END, e.city")
    Page<Equipments> findAllOrderByCityFirst(String city, Pageable pageable);

    Page<Equipments> findByEquipNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(String searchString, Pageable pageable, double quantity);

    List<Equipments> findByUserAndDeleteFlag(User user, boolean inDraft);
}
