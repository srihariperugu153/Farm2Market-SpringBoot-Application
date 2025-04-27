package com.farmtomarket.application.service;

import com.farmtomarket.application.dto.OrderDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.request.OrderProductDTO;
import org.springframework.data.domain.Pageable;

public interface OrderService {


    String validatePendingAmountAndProcessPayment(int orderId, double userPendingAmount);

    String orderProduct(OrderProductDTO orderProductDTO);

    PageResponse<OrderDTO> getEquipmentOrders(Pageable pageable);

    PageResponse<OrderDTO> getProductOrders(Pageable pageable);

}
