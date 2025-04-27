package com.farmtomarket.application.controller;

import com.farmtomarket.application.dto.EquipmentDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.request.EquipmentRequestDTO;
import com.farmtomarket.application.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {

        this.equipmentService = equipmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createEquipment(@RequestBody @Valid EquipmentRequestDTO equipmentRequest){
        return ResponseEntity.ok(equipmentService.createEquipment(equipmentRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<EquipmentDTO> updateEquipment(@RequestBody EquipmentDTO equipmentRequest){
        return ResponseEntity.ok(equipmentService.replaceEquipmentDetails(equipmentRequest));
    }

    @DeleteMapping("/removeOrRecover")
    public ResponseEntity<String> removeProduct(@RequestParam int equipmentId, boolean toDelete){
        return ResponseEntity.ok(equipmentService.removeOrRecoverEquipment(equipmentId,toDelete));
    }

    @GetMapping("/equipments")
    public ResponseEntity<PageResponse<EquipmentDTO>> equipmentList(@RequestParam(required = false,defaultValue = "0") int pageNo,
                                                                    @RequestParam(required = false,defaultValue = "10") int pageSize,
                                                                    @RequestParam(required = false) String searchString,
                                                                    @RequestParam(required = false) String city){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return ResponseEntity.ok(equipmentService.getEquipments(pageable,searchString,city));

    }

    @GetMapping("/myEquipment")
    public ResponseEntity<List<EquipmentDTO>> ownedEquipments(@RequestParam boolean inDraft){
        return ResponseEntity.ok(equipmentService.ownedEquipments(inDraft));
    }



}
