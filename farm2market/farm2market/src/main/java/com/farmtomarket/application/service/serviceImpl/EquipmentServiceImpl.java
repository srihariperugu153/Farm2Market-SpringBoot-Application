package com.farmtomarket.application.service.serviceImpl;

import com.farmtomarket.application.config.UserContextHolder;
import com.farmtomarket.application.dto.EquipmentDTO;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.dto.request.BookEquipmentDTO;
import com.farmtomarket.application.dto.request.EquipmentRequestDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.*;
import com.farmtomarket.application.repository.EquipmentOrderRepository;
import com.farmtomarket.application.repository.EquipmentRepository;
import com.farmtomarket.application.repository.TransactionRepository;
import com.farmtomarket.application.service.EquipmentService;
import io.swagger.v3.oas.models.info.License;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final TransactionRepository transactionRepository;
    private final EquipmentOrderRepository equipmentOrderRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, TransactionRepository transactionRepository, EquipmentOrderRepository equipmentOrderRepository, ModelMapper modelMapper) {
        this.equipmentRepository = equipmentRepository;
        this.transactionRepository = transactionRepository;
        this.equipmentOrderRepository = equipmentOrderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String createEquipment(EquipmentRequestDTO equipmentRequest){
        validatedUserAccess(UserContextHolder.getUserDto(),false,null);
        Equipments equipment = new Equipments();
        equipment.setEquipName(equipmentRequest.getEquipName());
        equipment.setQuantity(equipmentRequest.getQuantity());
        equipment.setRentCost(equipmentRequest.getRentCost());
        equipment.setCity(equipmentRequest.getCity());
        equipment.setState(equipmentRequest.getState());
        equipment.setPinCode(equipmentRequest.getPinCode());
        equipment.setUser(modelMapper.map(UserContextHolder.getUserDto(), User.class));
        equipmentRepository.save(equipment);
        return "Equipment Added";
    }

    @Override
    public EquipmentDTO replaceEquipmentDetails(EquipmentDTO equipmentRequest){
        Equipments equipment = equipmentRepository.findById(equipmentRequest.getEquipId())
                .orElseThrow(() -> new ApplicationException("Equipment Not Found"));
        validatedUserAccess(UserContextHolder.getUserDto(),true,equipment);
        equipment.setEquipName(equipmentRequest.getEquipName());
        equipment.setQuantity(equipmentRequest.getQuantity());
        equipment.setRentCost(equipmentRequest.getRentCost());
        equipment.setCity(equipmentRequest.getCity());
        equipment.setState(equipmentRequest.getState());
        equipment.setPinCode(equipmentRequest.getPinCode());
        equipmentRepository.save(equipment);
        return modelMapper.map(equipment,EquipmentDTO.class);
    }

    private static void validatedUserAccess(UserDTO userDTO, boolean isEdit, Equipments equipment){
        if (!userDTO.getRoleName().equals(Role.FARMER_EQUIPMENT.toString())){
            throw new SystemAuthException("Only Equipment Owners can add equipments");
        }
        if (isEdit && !equipment.getUser().getUsername().equals(userDTO.getUsername())){
            throw new SystemAuthException("You are not the Owner of the Equipment");
        }
    }

    @Override
    public PageResponse<EquipmentDTO> getEquipments(Pageable pageable, String searchString, String city){
        Page<Equipments> equipments;
        equipments = getEquipmentsPage(pageable, searchString, city);
        PageResponse<EquipmentDTO> pageResponse = new PageResponse<>();
        pageResponse.setContent(equipments.getContent().stream()
                .map(equipment -> modelMapper.map(equipment, EquipmentDTO.class))
                .toList());
        pageResponse.setPageNumber(equipments.getNumber());
        pageResponse.setPageSize(equipments.getSize());
        pageResponse.setTotalElements(equipments.getTotalElements());
        pageResponse.setTotalPages(equipments.getTotalPages());

        return pageResponse;
    }

    @Override
    @Transactional
    public String bookEquipment(BookEquipmentDTO bookEquipment){
        UserDTO userDTO = UserContextHolder.getUserDto();
        if (!userDTO.getRoleName().equals(Role.FARMER.toString())){
            throw new SystemAuthException(userDTO.getRoleName()+" is not allowed to book equipments");
        }
        Equipments equipments = equipmentRepository.findById(bookEquipment.getEquipmentId())
                .orElseThrow(() -> new ApplicationException("Equipment Not Found"));
        if (equipments.getQuantity() >= bookEquipment.getQuantity()){
            validateDates(bookEquipment);
            equipments.setQuantity(equipments.getQuantity() - bookEquipment.getQuantity());
            EquipmentOrder equipmentOrder = new EquipmentOrder();
            equipmentOrder.setEquipment(equipments);
            equipmentOrder.setEquipmentQuantity(bookEquipment.getQuantity());
            equipmentOrder.setFromDate(bookEquipment.getFromDate());
            equipmentOrder.setToDate(bookEquipment.getToDate());
            equipmentOrder.setEquipmentOrderedFarmer(modelMapper.map(userDTO, User.class));
            equipmentRepository.save(equipments);
            equipmentOrderRepository.save(equipmentOrder);
            paymentTransaction(bookEquipment,equipments,equipmentOrder);
            return "Equipment Booked";
        } else {
            throw new ApplicationException("Equipment Sold out");
        }
    }

    private static void validateDates(BookEquipmentDTO bookEquipment){
        if (bookEquipment.getFromDate().isAfter(bookEquipment.getToDate())){
            throw new ApplicationException("The 'toDate' must be later than the 'fromDate'. Please select a valid date range.");
        }
    }

    private void paymentTransaction(BookEquipmentDTO bookEquipment, Equipments equipments, EquipmentOrder equipmentOrder){
        Transaction transaction = new Transaction();
        LocalDate now = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(bookEquipment.getFromDate(),bookEquipment.getToDate());
        long value = daysBetween <= 0 ? 1 : daysBetween+1;
        double amount = (equipments.getRentCost() * bookEquipment.getQuantity())*value;
        if (amount != bookEquipment.getReceivedPayment()){
            throw new ApplicationException("Payment Required : Please ensure the total Amount of "+ amount+" is paid to proceed..");
        }
        transaction.setOrderId(equipmentOrder.getOrderId());
        transaction.setAmount(amount);
        transactionRepository.save(transaction);
    }

    private Page<Equipments> getEquipmentsPage(Pageable pageable, String searchString, String city){
        Page<Equipments> equipments;
        if (StringUtils.isNotBlank(city)){
            if (StringUtils.isBlank(searchString)){
                equipments = equipmentRepository.findByCityContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(city,pageable,0);
            } else {
                equipments = equipmentRepository.findByCityContainingIgnoreCaseAndEquipNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(city,searchString,pageable,0);
            }
        } else {
            if (StringUtils.isBlank(searchString)){
                equipments = equipmentRepository.findAllOrderByCityFirst(UserContextHolder.getUserDto().getCity(), pageable);
            } else {
                equipments = equipmentRepository.findByEquipNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(searchString, pageable,0);
            }
        }

        return equipments;
    }

    @Override
    public String returnEquipment(int orderId){

        EquipmentOrder equipmentOrder = equipmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException("Order ID : "+ orderId + " is not found"));
        validateTraderAccess(UserContextHolder.getUserDto(),equipmentOrder);
        if (equipmentOrder.isReturnStatus()){
            return "Equipment for this order is already been returned..";
        }
        Equipments equipment = equipmentOrder.getEquipment();
        LocalDate now = LocalDate.now();
        if (now.isAfter(equipmentOrder.getToDate())){
            long daysBetween = ChronoUnit.DAYS.between(equipmentOrder.getToDate(), now);
            throw new ApplicationException("Pay the pending Amount "+ daysBetween * (equipment.getRentCost() * equipmentOrder.getEquipmentQuantity()));
        }
        equipment.setQuantity(equipment.getQuantity() + equipmentOrder.getEquipmentQuantity());
        equipmentOrder.setReturnStatus(true);
        equipmentRepository.save(equipment);
        equipmentOrderRepository.save(equipmentOrder);
        return "Equipment Returned";
    }

    private void validateTraderAccess(UserDTO userDTO, EquipmentOrder equipmentOrder){
        if (!userDTO.getRoleName().equals(Role.FARMER.toString())){
            throw new SystemAuthException("Only farmer allowed to return Equipment");
        }
        if (!StringUtils.equals(equipmentOrder.getEquipmentOrderedFarmer().getUsername(), userDTO.getUsername())){
            throw new SystemAuthException("Only Owner of this Equipment can return this equipment");
        }
    }

    @Override
    public String removeOrRecoverEquipment(int equipmentId, boolean toDelete){
        Equipments equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ApplicationException("Equipment not found"));
        validatedUserAccess(UserContextHolder.getUserDto(),true,equipment);
        equipment.setDeleteFlag(toDelete);
        equipmentRepository.save(equipment);
        return toDelete ? "Equipment Removed ":"Equipment Recovered";
    }

    @Override
    public List<EquipmentDTO> ownedEquipments(boolean inDraft){
        User user = modelMapper.map(UserContextHolder.getUserDto(), User.class);
        if (!user.getRoleName().equals(Role.FARMER_EQUIPMENT)){
            throw new SystemAuthException(user.getRoleName()+" role is not allowed to Access");
        }
        List<Equipments> equipments = equipmentRepository.findByUserAndDeleteFlag(user,inDraft);
        return equipments.parallelStream().map(equipment -> modelMapper.map(equipment, EquipmentDTO.class)).toList();
    }
}
