package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "C_SHIPPINGMETHOD")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShippingMethod implements Serializable {

    private static final long serialVersionUID = -4091878249570461242L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSHIPPINGMETHOD")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "KEY")
    private String key;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "REQUIRE_ADDRESS")
    private boolean requireAddress;

    @Column(name = "HAVE_TRACKING")
    private boolean tracking;
}
