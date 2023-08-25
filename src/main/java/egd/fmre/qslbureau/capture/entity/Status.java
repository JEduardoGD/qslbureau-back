package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "C_STATUS")
@EqualsAndHashCode(of = { "id" })
public class Status implements Serializable {

    private static final long serialVersionUID = -5385977983842385859L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSTATUS")
    private Integer id;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "OBJECT")
    private String object;
    
    public Status() {
        super();
    }

    public Status(Integer id) {
        super();
        this.id = id;
    }
}
