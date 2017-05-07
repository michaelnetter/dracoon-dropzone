package org.mn.dropzone.rest.model;

public class DownloadShare {
    public Long id;
    public Long nodeId;
    public String name;
    public Integer classification;
    public String accessKey;
    public String notes;
    public Boolean showCreatorName;
    public Boolean showCreatorUsername;
    public Boolean isProtected;
    public Boolean notifyCreator;
    public String expireAt;
    public Integer maxDownloads;
    public Integer cntDownloads;
    public String recipients;
    public String createdAt;
    public UserInfo createdBy;
    public String nodePath;
    public Boolean isEncrypted;
}
