package com.lbadvisors.pffc.delivery_stops;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryStopsService {

    @Autowired
    DeliveryStopRepository deliveryStopsRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<DeliveryStopGetDto> findByDriverNameAndDeliveryDate(String driverName,
            LocalDate deliveryDate) {

        return this.deliveryStopsRepository.findByDriverNameAndDeliveryDate(driverName,
                deliveryDate).stream().map(
                        (deliveryStop) -> modelMapper.map(
                                deliveryStop,
                                DeliveryStopGetDto.class))
                .collect(Collectors.toList());
    }

    public Optional<DeliveryStopGetDto> updateArrivalTime(int id) {

        Optional<DeliveryStop> deliveryStopOptional = this.deliveryStopsRepository.findById(id);

        deliveryStopOptional.ifPresent(deliveryStop -> {
            deliveryStop.setActualArrivalTime(LocalDateTime.now());
            deliveryStopsRepository.save(deliveryStop);
        });

        return deliveryStopOptional.map(deliveryStop -> modelMapper.map(deliveryStop, DeliveryStopGetDto.class));

    }

    // @Override
    // public Optional<Routes> getRoute(int id) {
    // return this.routesRepository.findById(id);
    // }
}
