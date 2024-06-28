package com.lbadvisors.pffc.profiles;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<ProfileGetDto> findByCustomerId(int customerId) {

        return this.profileRepository.findByCustomerId(
                customerId).stream().map(
                        (profile) -> modelMapper.map(
                                profile,
                                ProfileGetDto.class))
                .collect(Collectors.toList());
    }

    // @Override
    // public Optional<Routes> getRoute(int id) {
    // return this.routesRepository.findById(id);
    // }
}
