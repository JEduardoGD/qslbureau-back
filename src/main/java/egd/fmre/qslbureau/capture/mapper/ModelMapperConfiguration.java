package egd.fmre.qslbureau.capture.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egd.fmre.qslbureau.capture.dto.LocalDto;
import egd.fmre.qslbureau.capture.entity.Local;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.addConverter(new AbstractConverter<Local, LocalDto>() {
            @Override
            protected LocalDto convert(Local local) {
                LocalDto localDto = new LocalDto();
                localDto.setId(local.getId());
                localDto.setMaxSlots(local.getMaxSlots());
                localDto.setName(local.getName());
                return localDto;
            }
        });
        return modelMapper;
    }
}
