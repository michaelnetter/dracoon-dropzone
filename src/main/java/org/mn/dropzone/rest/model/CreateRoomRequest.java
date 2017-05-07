package org.mn.dropzone.rest.model;

public class CreateRoomRequest {
    public Long parentId;
    public String name;
    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;
    public Long quota;
    public Long[] adminIds;
}
