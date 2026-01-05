package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class AttachmentObject implements Serializable {
    /**
     * W
     * 
     */
    private static final long serialVersionUID = 1L;
    private String filename;
    private byte[] data;

    public AttachmentObject(String filename, byte[] data) {
	this.filename = filename;
	this.data = data;
    }
}
