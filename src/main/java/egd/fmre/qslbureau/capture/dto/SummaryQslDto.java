package egd.fmre.qslbureau.capture.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SummaryQslDto {
    private String callsign;
    private Date oldest;
    private Date newest;
    private int count;
}
