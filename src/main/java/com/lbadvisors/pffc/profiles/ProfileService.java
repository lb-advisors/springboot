package com.lbadvisors.pffc.profiles;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.customers.CustomerGetDto;
import com.lbadvisors.pffc.sales_reps.SalesRepGetDto;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProfileService {

        @Autowired
        ProfileRepository profileRepository;

        @Autowired
        ModelMapper modelMapper;

        public ProfileGetDto findById(Integer profileDid) {

                Profile profile = this.profileRepository.findById(profileDid).orElseThrow(
                                () -> new EntityNotFoundException("Entity with id " + profileDid + " not found"));
                return modelMapper.map(
                                profile,
                                ProfileGetDto.class);

        }

        public List<ProfileGetDto> findByCustomerId(int customerId) {

                List<Profile> profiles = this.profileRepository.findByCustomerId(customerId);

                return profiles.stream().map(
                                (profile) -> {
                                        ProfileGetDto profileGetDto = modelMapper.map(
                                                        profile,
                                                        ProfileGetDto.class);
                                        return profileGetDto;
                                })
                                .collect(Collectors.toList());
        }

        public List<SalesRepGetDto> getAllSalesRepNames() {
                return this.profileRepository.findDistinctSalesRepNames().stream().map(
                                (profile) -> modelMapper.map(
                                                profile,
                                                SalesRepGetDto.class))
                                .collect(Collectors.toList());
        }

        public List<CustomerGetDto> getAllCustomers(String salesRepName) {
                return profileRepository.findDistinctCustomerIds(salesRepName).stream().map(
                                (profile) -> modelMapper.map(
                                                profile,
                                                CustomerGetDto.class))
                                .collect(Collectors.toList());
        }

        // @Override
        // public Optional<Routes> getRoute(int id) {
        // return this.routesRepository.findById(id);
        // }
}
