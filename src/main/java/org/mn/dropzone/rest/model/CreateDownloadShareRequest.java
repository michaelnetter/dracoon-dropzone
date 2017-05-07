package org.mn.dropzone.rest.model;

public class CreateDownloadShareRequest {
    public Long fileId;
    public Long nodeId;
    public String name;
    public String password;
    public Expiration expiration;
    public String notes;
    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean notifyCreator;
    public Integer maxDownloads;
    public Boolean sendMail;
    public String mailRecipients;
    public String mailSubject;
    public String mailBody;
}
