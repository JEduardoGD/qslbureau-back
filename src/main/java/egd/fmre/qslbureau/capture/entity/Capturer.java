package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "C_CAPTURER")
public class Capturer implements Serializable {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 1671542222670208320L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCAPTURER")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastName;
    
    @Column(name = "USERNAME")
    private String username;
    
    @Column(name = "PASSWORD")
    private String password;
    
    @Column(name = "START")
    private Date start;
    
    @Column(name = "END")
    private Date end;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "capturer")
    @JsonIgnore
    private Set<CapturerLocal> capturerLocals = new HashSet<>();
}
