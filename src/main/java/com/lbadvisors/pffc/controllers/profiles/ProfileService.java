package com.lbadvisors.pffc.controllers.profiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.controllers.customers.CustomerGetDto;
import com.lbadvisors.pffc.controllers.sales_reps.SalesRepGetDto;

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

                ProfileGetDto profileGetDto = modelMapper.map(
                                profile,
                                ProfileGetDto.class);

                ProfileDto profileDto = modelMapper.map(
                                profile,
                                ProfileDto.class);

                profileGetDto.setProfiles(Arrays.asList(profileDto));

                List<ShipToGetDto> shipToGetDtos = profile.getShipTos().stream().map(
                                (shipTo) -> {
                                        ShipToGetDto shipToGetDto = modelMapper.map(
                                                        shipTo,
                                                        ShipToGetDto.class);
                                        return shipToGetDto;
                                })
                                .collect(Collectors.toList());

                profileGetDto.setShipTos(shipToGetDtos);
                return profileGetDto;

        }

        public ProfileGetDto findByCustomerId(int customerId) {

                List<Profile> profiles = this.profileRepository.findByCustomerId(customerId);

                if (profiles.isEmpty()) {
                        new EntityNotFoundException("Entity with id " + customerId + " not found");
                }

                ProfileGetDto profileGetDto = new ProfileGetDto();
                profileGetDto.setCustomerId(profiles.get(0).getCustomerId());
                profileGetDto.setCustomerName(profiles.get(0).getCustomerName());
                profileGetDto.setSalesRepName(profiles.get(0).getSalesRepName());
                profileGetDto.setSalesRepPhone(profiles.get(0).getSalesRepPhone());

                List<ProfileDto> profileDtos = profiles.stream().map(
                                (profile) -> {
                                        ProfileDto profileDto = modelMapper.map(
                                                        profile,
                                                        ProfileDto.class);
                                        return profileDto;
                                })
                                .collect(Collectors.toList());

                profileGetDto.setProfiles(profileDtos);

                List<ShipToGetDto> shipToGetDtos = profiles.get(0).getShipTos().stream().map(
                                (shipTo) -> {
                                        ShipToGetDto shipToGetDto = modelMapper.map(
                                                        shipTo,
                                                        ShipToGetDto.class);
                                        return shipToGetDto;
                                })
                                .collect(Collectors.toList());

                profileGetDto.setShipTos(shipToGetDtos);

                return profileGetDto;
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