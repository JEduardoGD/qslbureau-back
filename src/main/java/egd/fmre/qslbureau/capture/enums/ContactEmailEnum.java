package egd.fmre.qslbureau.capture.enums;

import lombok.Getter;

public enum ContactEmailEnum {

	CANT_OBTAIN_EMAIL_FROM_QRZ(1), EMAIL_DOES_NOT_NEED_UPDATE(2), EMAIL_CAN_BE_UPDATE(3);

	@Getter
	private int value;

	ContactEmailEnum(int value) {
		this.value = value;
	}
}
