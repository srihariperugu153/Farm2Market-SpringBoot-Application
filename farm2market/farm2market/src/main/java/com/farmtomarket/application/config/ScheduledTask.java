package com.farmtomarket.application.config;

import com.farmtomarket.application.model.EquipmentOrder;
import com.farmtomarket.application.repository.EquipmentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class ScheduledTask {

    private final EquipmentOrderRepository equipmentOrderRepository;

    @Autowired
    public ScheduledTask(EquipmentOrderRepository equipmentOrderRepository){
        this.equipmentOrderRepository = equipmentOrderRepository;
    }

    public void equipmentOrdersDueDatesUpdate(){
        List<EquipmentOrder> equipmentOrders = equipmentOrderRepository.findAll();
        LocalDate now = LocalDate.now();
        equipmentOrders.parallelStream()
                .filter(equipmentOrder -> equipmentOrder.getToDate().isBefore(now))
                .forEach(equipmentOrder -> equipmentOrder.setDueDays((int) ChronoUnit.DAYS.between(equipmentOrder.getToDate(),now)));
        equipmentOrderRepository.saveAll(equipmentOrders);
    }

}
