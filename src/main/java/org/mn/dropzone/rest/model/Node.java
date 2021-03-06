package org.mn.dropzone.rest.model;

public class Node {
    public Long id;
    public String type;
    public Long parentId;
    public String parentPath;
    public String createdAt;
    public UserInfo createdBy;
    public String updatedAt;
    public UserInfo updatedBy;
    public String expireAt;
    public String name;
    public String hash;
    public String fileType;
    public Long size;
    public Integer classification;
    public String notes;
    public NodePermissions permissions;
    public Boolean isEncrypted;
    public Integer cntChildren;
    public Integer cntDeletedVersions;
    public Boolean hasRecycleBin;
    public Integer recycleBinRetentionPeriod;
    public Long quota;
    public Integer cntDownloadShares;
    public Integer cntUploadShares;
    public Boolean isFavorite;
    public Boolean inheritPermissions;
    public EncryptionInfo encryptionInfo;
    public Long branchVersion;
}
