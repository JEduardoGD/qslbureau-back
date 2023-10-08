package egd.fmre.qslbureau.capture.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egd.fmre.qslbureau.capture.dto.LocalDto;
import egd.fmre.qslbureau.capture.dto.ShipDto;
import egd.fmre.qslbureau.capture.dto.ShippingMethodDto;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.ShippingMethod;

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

    @Bean
    ModelMapper shippingMethodModelMapper() {
        ModelMapper shippingMethodModelMapper = new ModelMapper();

        shippingMethodModelMapper.addConverter(new AbstractConverter<ShippingMethod, ShippingMethodDto>() {
            @Override
            protected ShippingMethodDto convert(ShippingMethod sp) {
                ShippingMethodDto spDto = new ShippingMethodDto();
                spDto.setId(sp.getId());
                spDto.setKey(sp.getKey());
                spDto.setName(sp.getName());
                spDto.setDescription(sp.getDescription());
                spDto.setHaveTracking(sp.isTracking());
                return spDto;
            }
        });
        return shippingMethodModelMapper;
    }

    @Bean
    ModelMapper shipModelMapper() {
        ModelMapper shipModelMapper = new ModelMapper();

        shipModelMapper.addConverter(new AbstractConverter<Ship, ShipDto>() {
            @Override
            protected ShipDto convert(Ship s) {
                ShipDto shipDto = new ShipDto();
                shipDto.setId(s.getId());
                shipDto.setDatetime(s.getDatetime());
                shipDto.setSlotId(s.getSlot() != null ? s.getSlot().getId() : null);
                shipDto.setShippingMethodId(s.getShippingMethod() != null ? s.getShippingMethod().getId() : null);
                shipDto.setZoneId(s.getZone() != null ? s.getZone().getId() : null);
                shipDto.setAddress(s.getAddress());
                shipDto.setTrackingCode(s.getTrackingCode());
                return shipDto;
            }
        });
        return shipModelMapper;
    }
}
