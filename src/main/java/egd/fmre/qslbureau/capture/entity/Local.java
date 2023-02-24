package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
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
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "local")
    private Set<CapturerLocal> capturerLocals = new HashSet<>();
}
