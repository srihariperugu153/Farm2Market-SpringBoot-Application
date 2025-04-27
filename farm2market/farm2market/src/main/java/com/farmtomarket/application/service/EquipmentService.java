package com.farmtomarket.application.service;

import com.farmtomarket.application.dto.EquipmentDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.request.BookEquipmentDTO;
import com.farmtomarket.application.dto.request.EquipmentRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentService {
    String createEquipment(EquipmentRequestDTO equipmentRequest);

    EquipmentDTO replaceEquipmentDetails(EquipmentDTO equipmentRequest);

    String removeOrRecoverEquipment(int equipmentId, boolean toDelete);

    PageResponse<EquipmentDTO> getEquipments(Pageable pageable, String searchString, String city);

    List<EquipmentDTO> ownedEquipments(boolean inDraft);

    String bookEquipment(BookEquipmentDTO bookEquipment);

    String returnEquipment(int orderId);
}
