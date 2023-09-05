package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "CAT_DXCC_ENTITY")
public class DxccEntity implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5379087720321735976L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "D_N_ID_DXCCENTITY")
    private Long id;

    @Column(name = "D_ENTITY_CODE")
    private Long entityCode;

    @Column(name = "S_ENTITY")
    private String entity;

    @Column(name = "S_CONT")
    private String cont;

    @Column(name = "D_ITU")
    private Integer itu;

    @Column(name = "D_CQ")
    private Integer cq;

    @Column(name = "S_ORIGEN")
    private String origen;

    @Column(name = "D_UPDATED_AT")
    private Date updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DxccEntity other = (DxccEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}
