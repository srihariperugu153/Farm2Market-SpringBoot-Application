package com.farmtomarket.application.repository;

import com.farmtomarket.application.model.EquipmentOrder;
import com.farmtomarket.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentOrderRepository extends JpaRepository<EquipmentOrder, Integer> {
    Page<EquipmentOrder> findByEquipmentOrderedFarmer(User loggedUser, Pageable pageable);
}
