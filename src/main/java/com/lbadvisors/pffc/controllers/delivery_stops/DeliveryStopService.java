package com.lbadvisors.pffc.controllers.delivery_stops;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lbadvisors.pffc.configuration.AwsProperties;
import com.lbadvisors.pffc.util.AwsService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DeliveryStopService {

        @Autowired
        DeliveryStopRepository deliveryStopsRepository;

        @Autowired
        ModelMapper modelMapper;

        @Autowired
        AwsService awsService;

        @Autowired
        AwsProperties awsProperties;

        public List<DeliveryStopGetDto> findByDriverNameAndDeliveryDate(String driverName,
                        LocalDate deliveryDate) {

                return this.deliveryStopsRepository.findByDriverNameAndDeliveryDateOrderByPriorityAsc(driverName,
                                deliveryDate).stream().map(
                                                (deliveryStop) -> {
                                                        DeliveryStopGetDto deliveryStopGetDto = modelMapper.map(
                                                                        deliveryStop,
                                                                        DeliveryStopGetDto.class);
                                                        if (deliveryStop.getS3FileKey() != null) {
                                                                deliveryStopGetDto.setFileUrl(awsProperties
                                                                                .getEndpointUrl()
                                                                                + deliveryStop.getS3FileKey());
                                                        }
                                                        return deliveryStopGetDto;
                                                })
                                .collect(Collectors.toList());
        }

        public DeliveryStopGetDto uploadPhoto(int deliveryStopId, MultipartFile multipartFile) {

                // TODO: compress the image
                DeliveryStop deliveryStop = this.deliveryStopsRepository.findById(
                                deliveryStopId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Entity with id " + deliveryStopId + " not found"));

                String driverName = deliveryStop.getDriverName().replaceAll("[^a-zA-Z0-9-_.~]", "_");
                String formattedDeliveryStopId = String.format("%010d", deliveryStopId);

                // Get the original filename and then the extension
                String originalFilename = multipartFile.getOriginalFilename();
                String fileExtension = StringUtils.getFilenameExtension(originalFilename);

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh-mm-ss-SSS");

                String s3FileKey = String.format("%s/%s/%s-%s.%s",
                                driverName, sdfDate.format(new Date()),
                                formattedDeliveryStopId, sdfTime.format(new Date()),
                                fileExtension);

                awsService.uploadFile(s3FileKey, multipartFile);

                deliveryStop.setActualArrivalTime(LocalDateTime.now());
                deliveryStop.setS3FileKey(s3FileKey);
                deliveryStopsRepository.save(deliveryStop);

                DeliveryStopGetDto deliveryStopGetDto = modelMapper.map(deliveryStop, DeliveryStopGetDto.class);
                deliveryStopGetDto.setFileUrl(awsProperties.getEndpointUrl() + s3FileKey);

                return deliveryStopGetDto;
        }
}
