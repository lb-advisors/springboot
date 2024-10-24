package com.lbadvisors.pffc.controllers.orders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.lbadvisors.pffc.controllers.profiles.ProfileGetDto;
import com.lbadvisors.pffc.controllers.profiles.ProfileService;
import com.lbadvisors.pffc.controllers.profiles.ShipToGetDto;
import com.lbadvisors.pffc.controllers.profiles.ShipToService;
import com.lbadvisors.pffc.exception.ResourceAlreadyExistsException;
import com.lbadvisors.pffc.util.EmailService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    ShipToService shipToService;

    @Autowired
    private EmailService emailService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public OrderGetDto saveOrder(@Valid OrderPostDto orderPostDto) {

        // check if there is alre
        Integer customerID = orderPostDto.getCustomerId();
        LocalDate deliveryDate = orderPostDto.getDeliveryDate();
        Integer shipToId = orderPostDto.getShipToId();

        // customers cannot place two orders for the same day / ship to
        List<Order> existingOrders = orderRepository.findByCustomerIdAndDeliveryDateAndShipToId(customerID, deliveryDate, shipToId);

        if (existingOrders.size() > 0) {

            OrderGetDto orderGetDto = new OrderGetDto(existingOrders.get(0).getOrderId(), existingOrders.get(0).getCustomerId(), existingOrders.get(0).getCustomerName(),
                    existingOrders.get(0).getSalesRepName(), existingOrders.get(0).getSalesRepPhone(), existingOrders.get(0).getDeliveryDate(), existingOrders.get(0).getShipToId(),
                    existingOrders.get(0).getShipToName(), existingOrders.get(0).getCustomerPo(), existingOrders.get(0).getTotalPrice(),
                    existingOrders.stream().map(order -> modelMapper.map(order, OrderProfileGetDto.class)).collect(Collectors.toList()));

            throw new ResourceAlreadyExistsException("There is already an order for that day.", orderGetDto);
        }

        Integer orderId = orderRepository.getNextOrderIdSequenceValue();

        List<Order> orders = orderPostDto.getProfiles().stream().map(orderProfilePostDto -> {

            ProfileGetDto profileGetDto = profileService.findById(orderProfilePostDto.getProfileDid());

            Order order = modelMapper.map(orderProfilePostDto, Order.class);

            order.setOrderId(orderId);

            order.setCustomerId(orderPostDto.getCustomerId());
            order.setDeliveryDate(orderPostDto.getDeliveryDate());
            order.setShipToId(orderPostDto.getShipToId());
            order.setCompanyId(profileGetDto.getCompanyId());
            order.setCustomerPo(orderPostDto.getCustomerPo());
            order.setTotalPrice(orderPostDto.getTotalPrice());
            order.setCustomerName(profileGetDto.getCustomerName());
            order.setCustomerEmail(profileGetDto.getCustomerEmail());
            order.setSalesRepName(profileGetDto.getSalesRepName());
            order.setSalesRepPhone(profileGetDto.getSalesRepPhone());
            order.setPackSize(profileGetDto.getProfiles().get(0).getPackSize());
            order.setProfileDescription(profileGetDto.getProfiles().get(0).getProfileDescription());
            order.setPrice(profileGetDto.getProfiles().get(0).getPrice());
            order.setUnitType(profileGetDto.getProfiles().get(0).getUnitType());

            if (orderPostDto.getShipToId() != null) {
                ShipToGetDto shipToGetDto = shipToService.findById(orderPostDto.getShipToId());
                order.setShipToName(shipToGetDto.getShipToName());
            }

            // order.setProfileId(profileGetDto.getProfileId());
            // TODO: add rest

            // order.setItemName(profileService.findById(itemDTO.getItemId()));

            return order;
        }).collect(Collectors.toList());

        List<Order> savedOrders = orderRepository.saveAll(orders);

        OrderGetDto orderGetDto = new OrderGetDto(savedOrders.get(0).getOrderId(), savedOrders.get(0).getCustomerId(), savedOrders.get(0).getCustomerName(),
                savedOrders.get(0).getSalesRepName(), savedOrders.get(0).getSalesRepPhone(), savedOrders.get(0).getDeliveryDate(), savedOrders.get(0).getShipToId(),
                savedOrders.get(0).getShipToName(), savedOrders.get(0).getCustomerPo(), savedOrders.get(0).getTotalPrice(),
                savedOrders.stream().map(order -> modelMapper.map(order, OrderProfileGetDto.class)).collect(Collectors.toList()));

        // send confirmation email
        String customerEmail = orders.get(0).getCustomerEmail();
        if (customerEmail != null && customerEmail.length() > 0) {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("order", orderGetDto);
            emailService.sendEmail(customerEmail, templateModel);
        }

        return orderGetDto;
    }
}
