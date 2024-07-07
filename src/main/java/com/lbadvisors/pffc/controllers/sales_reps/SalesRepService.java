package com.lbadvisors.pffc.controllers.sales_reps;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;

@Service
public class SalesRepService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<SalesRepGetDto> getAllSalesRepNames() {
        return this.profileRepository.findDistinctSalesRepNames().stream().map(
                (profile) -> modelMapper.map(
                        profile,
                        SalesRepGetDto.class))
                .collect(Collectors.toList());
    }

}
