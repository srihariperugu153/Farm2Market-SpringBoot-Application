package com.farmtomarket.application.controller;

import com.farmtomarket.application.config.UserContextHolder;
import com.farmtomarket.application.dto.OrderDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.request.BookEquipmentDTO;
import com.farmtomarket.application.dto.request.OrderProductDTO;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.Role;
import com.farmtomarket.application.service.EquipmentService;
import com.farmtomarket.application.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    private final OrderService orderService;
    private final EquipmentService equipmentService;

    @Autowired
    public OrderController(OrderService orderService, EquipmentService equipmentService) {
        this.orderService = orderService;
        this.equipmentService = equipmentService;
    }

    @PostMapping("/pendingPaymentAmount")
    public ResponseEntity<String> equipmentPendingPaymentAmount(@RequestParam int orderId,
                                                                @RequestParam double userPendingAmount){
        return ResponseEntity.ok(orderService.validatePendingAmountAndProcessPayment(orderId,userPendingAmount));
    }

    @PostMapping("/orderProduct")
    public ResponseEntity<String> orderProduct(@RequestBody OrderProductDTO orderProductDTO){
        return ResponseEntity.ok(orderService.orderProduct(orderProductDTO));
    }

    @GetMapping("/orders")
    public ResponseEntity<PageResponse<OrderDTO>> getOrdersByUserType(@RequestParam(defaultValue = "0")int page,
                                                                      @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page,size);
        PageResponse<OrderDTO> orders;
        String userRole = UserContextHolder.getUserDto().getRoleName();
        if(Role.FARMER.toString().equals(userRole)){
            orders = orderService.getEquipmentOrders(pageable);
        } else if (Role.TRADER.toString().equals(userRole)){
            orders = orderService.getProductOrders(pageable);
        } else {
            throw new SystemAuthException("Farming Equipment Owner Cannot view the orders history");
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/bookEquipment")
    public ResponseEntity<String> bookEquipment(@RequestBody @Valid BookEquipmentDTO bookEquipment){
        return ResponseEntity.ok(equipmentService.bookEquipment(bookEquipment));
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<String> returnEquipmentByOrderId(@PathVariable int orderId){
        return ResponseEntity.ok(equipmentService.returnEquipment(orderId));
    }

}
