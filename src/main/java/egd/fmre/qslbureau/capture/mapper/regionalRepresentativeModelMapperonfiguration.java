package egd.fmre.qslbureau.capture.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egd.fmre.qslbureau.capture.dto.RegionalRepresentativeDto;
import egd.fmre.qslbureau.capture.entity.Capturer;

@Configuration
public class regionalRepresentativeModelMapperonfiguration {
    @Bean
    ModelMapper regionalRepresentativeModelMapper() {
        ModelMapper regionalRepresentativeModelMapper = new ModelMapper();

        regionalRepresentativeModelMapper.addConverter(new AbstractConverter<Capturer, RegionalRepresentativeDto>() {

            @Override
            protected RegionalRepresentativeDto convert(Capturer capturer) {
                RegionalRepresentativeDto rr = new RegionalRepresentativeDto();
                rr.setId(capturer.getId());
                rr.setName(String.format("%s %s",capturer.getName(),capturer.getLastName()));
                rr.setLastname(capturer.getLastName());
                rr.setUsername(capturer.getUsername());
                return rr;
            }
        });
        return regionalRepresentativeModelMapper;
    }
}
