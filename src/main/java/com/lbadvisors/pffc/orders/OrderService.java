package com.lbadvisors.pffc.orders;

import java.util.List;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.lbadvisors.pffc.profiles.ProfileGetDto;
import com.lbadvisors.pffc.profiles.ProfileService;
import com.lbadvisors.pffc.profiles.ShipToGetDto;
import com.lbadvisors.pffc.profiles.ShipToService;
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

    // @Autowired
    // private EmailService emailService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public OrderGetDto saveOrder(@Valid OrderPostDto orderPostDto) {

        Integer orderId = orderRepository.getNextOrderIdSequenceValue();

        List<Order> orders = orderPostDto.getOrderProfiles().stream().map(orderProfilePostDto -> {
            ProfileGetDto profileGetDto = profileService.findById(orderProfilePostDto.getProfileDid());

            Order order = modelMapper.map(orderProfilePostDto, Order.class);

            order.setOrderId(orderId);

            order.setCustomerId(orderPostDto.getCustomerId());
            order.setDeliveryDate(orderPostDto.getDeliveryDate());
            order.setShipToId(orderPostDto.getShipToId());
            order.setTotalPrice(orderPostDto.getTotalPrice());
            order.setCustomerName(profileGetDto.getCustomerName());
            order.setCustomerEmail(profileGetDto.getCustomerEmail());
            order.setSalesRepName(profileGetDto.getSalesRepName());
            order.setSalesRepPhone(profileGetDto.getSalesRepPhone());

            order.setPackSize(profileGetDto.getProfiles().get(0).getPackSizePd());
            order.setProfileDescription(profileGetDto.getProfiles().get(0).getProfileDescription());
            order.setPrice(profileGetDto.getProfiles().get(0).getSalesPrice());
            order.setUnitType(profileGetDto.getProfiles().get(0).getUnitTypePd());

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

        // send confirmation email
        // emailService.sendEmail("oleblond@gmail.com", "Test", "Email Body");

        OrderGetDto orderGetDto = new OrderGetDto(
                savedOrders.get(0).getOrderId(),
                savedOrders.get(0).getCustomerId(),
                savedOrders.get(0).getCustomerName(),
                savedOrders.get(0).getSalesRepName(),
                savedOrders.get(0).getSalesRepPhone(),
                savedOrders.get(0).getDeliveryDate(),
                savedOrders.get(0).getShipToId(),
                savedOrders.get(0).getShipToName(),
                savedOrders.stream()
                        .map(order -> modelMapper.map(order, OrderProfileGetDto.class))
                        .collect(Collectors.toList()));

        return orderGetDto;
    }
}
