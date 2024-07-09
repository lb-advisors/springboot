package com.lbadvisors.pffc.controllers.company;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;

@Service
public class CompanyService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<CompanyGetDto> getAllCompanies() {
        return this.profileRepository.findDistinctCompanies().stream().map(
                (company) -> modelMapper.map(
                        company,
                        CompanyGetDto.class))
                .collect(Collectors.toList());
    }

}
