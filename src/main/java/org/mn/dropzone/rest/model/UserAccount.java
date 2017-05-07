package org.mn.dropzone.rest.model;

public class UserAccount {
    public Long id;
    public CustomerData customer;
    public Boolean isEncryptionEnabled;
    public Boolean needsToChangePassword;
    public Boolean needsToAcceptEULA;
    public Boolean needsToChangeUserName;
    public Boolean hasManageableRooms;
    public RoleList userRoles;
    public Integer lockStatus;
    public String expireAt;
    public String login;
    public String email;
    public String title;
    public String gender;
    public String firstName;
    public String lastName;
    public String lastLoginSuccessAt;
    public String lastLoginSuccessIp;
    public String lastLoginFailAt;
    public String lastLoginFailIp;
    public UserGroup[] userGroups;
}
