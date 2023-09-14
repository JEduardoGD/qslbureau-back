package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CapturerDTO implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5352432641681581703L;

    private Integer id;

    private String name;

    private String lastName;

    private String username;

    private Date start;

    private Date end;

}
