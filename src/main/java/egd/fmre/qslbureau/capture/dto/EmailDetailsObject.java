package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.List;

import javax.mail.Session;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDetailsObject implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7646999604787019726L;
    private Session session;
    private String emailFromAddress;
    private String emailFromName;
    private String emailTo;
    private String subject;
    private String body;
    private List<AttachmentObject> attachments;
}