package com.lbadvisors.pffc.drivers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.delivery_stops.DeliveryStopRepository;

@Service
public class DriverService {

    @Autowired
    DeliveryStopRepository deliveryStopRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<String> findAll() {

        return this.deliveryStopRepository.findDistinctDriverNames();
        /*
         * .stream().map(
         * (driver) -> modelMapper.map(
         * driver,
         * DriverGetDto.class))
         * .collect(Collectors.toList());
         * 
         * 
         */
    }

}
