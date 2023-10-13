package egd.fmre.qslbureau.capture.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egd.fmre.qslbureau.capture.dto.ZoneruleDto;
import egd.fmre.qslbureau.capture.entity.Zonerule;

@Configuration
public class ZoneruleMapperConfiguration {
    @Bean
    ModelMapper zoneruleModelMapper() {
        ModelMapper zoneruleModelMapper = new ModelMapper();

        zoneruleModelMapper.addConverter(new AbstractConverter<Zonerule, ZoneruleDto>() {

            @Override
            protected ZoneruleDto convert(Zonerule zonerule) {
                ZoneruleDto zoneruleDto = new ZoneruleDto();
                zoneruleDto.setId(zonerule.getId());
                zoneruleDto.setCapturerId(zonerule.getCapturer() != null ? zonerule.getCapturer().getId() : null);
                zoneruleDto.setZoneId(zonerule.getZone() != null ? zonerule.getZone().getId() : null);
                zoneruleDto.setCallsign(zonerule.getCallsign());
                zoneruleDto.setStart(zonerule.getStart());
                zoneruleDto.setEnd(zonerule.getEnd());
                return zoneruleDto;
            }
        });
        return zoneruleModelMapper;
    }
}
