package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QslSumatoryDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2738832489728695529L;
	private Integer localId;
	private Integer slotNumber;
	private String toCallsign;
	private String via;
	private Integer c;
	@Override
	public int hashCode() {
		return Objects.hash(localId, slotNumber, toCallsign, via);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QslSumatoryDto other = (QslSumatoryDto) obj;
		return Objects.equals(localId, other.localId) && Objects.equals(slotNumber, other.slotNumber)
				&& Objects.equals(toCallsign, other.toCallsign) && Objects.equals(via, other.via);
	}
}
