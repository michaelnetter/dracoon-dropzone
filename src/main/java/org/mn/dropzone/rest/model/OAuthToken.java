package org.mn.dropzone.rest.model;

import com.google.gson.annotations.SerializedName;

public class OAuthToken {

    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("refresh_token")
    public String refreshToken;
    @SerializedName("expires_in")
    public Integer expiresIn;
    @SerializedName("scope")
    public String scope;

    @Override
    public String toString() {
        return "OAuthTokenResponse {" +
                "accessToken=" + accessToken + ", " +
                "tokenType='" + tokenType + ", " +
                "refreshToken='" + refreshToken + ", " +
                "expiresIn=" + expiresIn + ", " +
                "scope=" + scope +
                '}';
    }

}
