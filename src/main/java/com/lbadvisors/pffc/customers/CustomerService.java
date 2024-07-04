package com.lbadvisors.pffc.customers;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.profiles.ProfileRepository;

@Service
public class CustomerService {

        @Autowired
        ProfileRepository profileRepository;

        @Autowired
        ModelMapper modelMapper;

        public List<CustomerGetDto> getAllCustomers(String salesRepName) {
                return this.profileRepository.findDistinctCustomerIds(salesRepName).stream().map(
                                (profile) -> modelMapper.map(
                                                profile,
                                                CustomerGetDto.class))
                                .collect(Collectors.toList());
        }

}
