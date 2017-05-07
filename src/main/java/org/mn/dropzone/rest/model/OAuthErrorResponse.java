package org.mn.dropzone.rest.model;

import com.google.gson.annotations.SerializedName;

public class OAuthErrorResponse {

    @SerializedName("error")
    public String error;
    @SerializedName("error_description")
    public String errorDescription;

    @Override
    public String toString() {
        return "OAuthErrorResponse{" +
                "error=" + error + ", " +
                "errorDescription='" + errorDescription +
                '}';
    }

}
