package com.lbadvisors.pffc.profiles;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ShipToService {

        @Autowired
        ShipToRepository shipToRepository;

        @Autowired
        ModelMapper modelMapper;

        public ShipToGetDto findById(Integer id) {

                ShipTo shipTo = shipToRepository.findById(id).orElseThrow(
                                () -> new EntityNotFoundException("Entity with id " + id + " not found"));
                return modelMapper.map(
                                shipTo,
                                ShipToGetDto.class);

        }

}
