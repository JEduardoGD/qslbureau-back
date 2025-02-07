package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "C_LOCAL")
@EqualsAndHashCode(of = {"id"})
public class Local implements Serializable {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -4700192066610543036L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLOCAL")
    private Integer id;
    
    @Column(name = "MAXSLOTS")
    private int maxSlots;
    
    @Column(name = "NAME")
    private String name;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "local")
    private Set<CapturerLocal> capturerLocals = new HashSet<>();
}
