package com.farmtomarket.application.service.serviceImpl;

import com.farmtomarket.application.config.UserContextHolder;
import com.farmtomarket.application.dto.OrderDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.dto.request.OrderProductDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.*;
import com.farmtomarket.application.repository.*;
import com.farmtomarket.application.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class OrderServiceImpl implements OrderService {

    private final EquipmentOrderRepository equipmentOrderRepository;

    private final TransactionRepository transactionRepository;

    private final EquipmentRepository equipmentRepository;

    private final ProductRepository productRepository;

    private final ProductOrderRepository productOrderRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(EquipmentOrderRepository equipmentOrderRepository, TransactionRepository transactionRepository, EquipmentRepository equipmentRepository, ProductRepository productRepository, ProductOrderRepository productOrderRepository, ModelMapper modelMapper) {
        this.equipmentOrderRepository = equipmentOrderRepository;
        this.transactionRepository = transactionRepository;
        this.equipmentRepository = equipmentRepository;
        this.productRepository = productRepository;
        this.productOrderRepository = productOrderRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public String validatePendingAmountAndProcessPayment(int orderId, double userPendingAmount) {
        EquipmentOrder equipmentOrder = equipmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException("Order ID : "+orderId+" is not found"));
        if (equipmentOrder.isReturnStatus()){
            return "Equipment for this order is already been returned..";
        }
        Equipments equipments = equipmentOrder.getEquipment();
        double dailyRate = equipments.getRentCost();

        long extraDays = ChronoUnit.DAYS.between(equipmentOrder.getToDate(), LocalDate.now());
        if (extraDays <= 0){
            return "No Pending Payment. Equipment was returned on time.. ";
        }
        double actualPendingAmount = extraDays * (dailyRate * equipmentOrder.getEquipmentQuantity());

        if (actualPendingAmount != userPendingAmount){
            throw new ApplicationException("You Entered inCorrect Amount.. Actual Amount you have to pay is Rs."+actualPendingAmount);
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(actualPendingAmount);
        transaction.setOrderId(equipmentOrder.getOrderId());
        transactionRepository.save(transaction);

        equipments.setQuantity(equipments.getQuantity() + equipmentOrder.getEquipmentQuantity());
        equipmentOrder.setReturnStatus(true);
        equipmentRepository.save(equipments);
        equipmentOrderRepository.save(equipmentOrder);

        return "Pending Payment against Order Id: "+orderId+" is "+actualPendingAmount+" Paid and Equipment is returned";
    }


    @Override
    public PageResponse<OrderDTO> getEquipmentOrders(Pageable pageable){
        Page<EquipmentOrder> equipmentOrders;
        equipmentOrders = getEquipmentOrdersPage(pageable);
        PageResponse<OrderDTO> pageResponse = new PageResponse<>();
        pageResponse.setContent(equipmentOrders.getContent().stream()
                .map(equipmentOrder -> {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setEquipmentName(equipmentOrder.getEquipment().getEquipName());
                    orderDTO.setOrderQuantity(equipmentOrder.getEquipmentQuantity());
                    orderDTO.setId(equipmentOrder.getOrderId());
                    orderDTO.setFromDate(equipmentOrder.getFromDate());
                    orderDTO.setToDate(equipmentOrder.getToDate());
                    orderDTO.setDueDays(equipmentOrder.getDueDays());
                    orderDTO.setUpdatedAt(equipmentOrder.getUpdatedAt());
                    orderDTO.setReturnStatus(equipmentOrder.isReturnStatus());
                    return orderDTO;
                }).toList());
        pageResponse.setPageNumber(equipmentOrders.getNumber());
        pageResponse.setPageSize(equipmentOrders.getSize());
        pageResponse.setTotalElements(equipmentOrders.getTotalElements());
        pageResponse.setTotalPages(equipmentOrders.getTotalPages());
        return pageResponse;
    }



    @Override
    @Transactional
    public String orderProduct(OrderProductDTO orderProductDTO){
        UserDTO userDTO = UserContextHolder.getUserDto();
        if (!userDTO.getRoleName().equals(Role.TRADER.toString())){
            throw new SystemAuthException("Only Trader is Allowed to Order Product ");
        }
        Products product = productRepository.findById(orderProductDTO.getProdId())
                .orElseThrow(() -> new ApplicationException("Product Not Found"));
        quantityValidation(orderProductDTO, product);
        double costRequired = product.getPrice() * orderProductDTO.getQuantity();
        if (costRequired == orderProductDTO.getAmount()){
            ProductOrder productOrder = new ProductOrder();
            productOrder.setProductQuantity(orderProductDTO.getQuantity());
            productOrder.setTrader(modelMapper.map(userDTO, User.class));
            productOrder.setProduct(product);
            product.setQuantity(product.getQuantity() - productOrder.getProductQuantity());
            productRepository.save(product);
            productOrderRepository.save(productOrder);
            Transaction transaction = new Transaction();
            transaction.setProduct(true);
            transaction.setOrderId(productOrder.getOrderId());
            transaction.setAmount(costRequired);
            transactionRepository.save(transaction);
            return "Product Ordered";
        } else {
            throw new ApplicationException("Payment Required is: "+ costRequired);
        }
    }




    private static void quantityValidation(OrderProductDTO orderProductDTO, Products product){
        if (product.getQuantity() <= 0){
            throw new ApplicationException("Product Sold Out");
        }
        if (product.getQuantity() < orderProductDTO.getQuantity()){
            throw new ApplicationException("The Quantity should be within "+ product.getQuantity());
        }
    }

    private Page<EquipmentOrder> getEquipmentOrdersPage(Pageable pageable){
        User loggedUser = modelMapper.map(UserContextHolder.getUserDto(),User.class);
        Page<EquipmentOrder> equipmentOrders;
        equipmentOrders = equipmentOrderRepository.findByEquipmentOrderedFarmer(loggedUser,pageable);
        return equipmentOrders;
    }

    @Override
    public PageResponse<OrderDTO> getProductOrders(Pageable pageable){
        Page<ProductOrder> productOrders;
        productOrders = getProductOrdersPage(pageable);
        PageResponse<OrderDTO> pageResponse = new PageResponse<>();
        pageResponse.setContent(productOrders.getContent().stream()
                .map(productOrder -> {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setId(productOrder.getOrderId());
                    orderDTO.setFromDate(productOrder.getCreatedAt());
                    orderDTO.setProductName(productOrder.getProduct().getProductName());
                    orderDTO.setOrderQuantity((int) productOrder.getProductQuantity());
                    return orderDTO;
                })
                .toList());
        pageResponse.setPageNumber(productOrders.getNumber());
        pageResponse.setPageSize(productOrders.getSize());
        pageResponse.setTotalElements(productOrders.getTotalElements());
        pageResponse.setTotalPages(productOrders.getTotalPages());
        return pageResponse;
    }

    private Page<ProductOrder> getProductOrdersPage(Pageable pageable){
        User loggedUser = modelMapper.map(UserContextHolder.getUserDto(),User.class);
        Page<ProductOrder> productOrders;
        productOrders = productOrderRepository.findByTraderId(loggedUser.getId(),pageable);
        return productOrders;
    }


}
