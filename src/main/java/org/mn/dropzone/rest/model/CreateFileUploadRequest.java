package org.mn.dropzone.rest.model;

public class CreateFileUploadRequest {
    public Long parentId;
    public String name;
    public Long size;
    public Expiration expiration;
    public Integer classification;
    public String notes;
}
