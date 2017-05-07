package org.mn.dropzone.rest;



import org.mn.dropzone.rest.model.AuthResources;
import org.mn.dropzone.rest.model.AuthToken;
import org.mn.dropzone.rest.model.CompleteFileUploadRequest;
import org.mn.dropzone.rest.model.CreateDownloadShareRequest;
import org.mn.dropzone.rest.model.CreateFileUploadRequest;
import org.mn.dropzone.rest.model.CreateFolderRequest;
import org.mn.dropzone.rest.model.CreateRoomRequest;
import org.mn.dropzone.rest.model.DownloadShare;
import org.mn.dropzone.rest.model.FileUpload;
import org.mn.dropzone.rest.model.LoginRequest;
import org.mn.dropzone.rest.model.Node;
import org.mn.dropzone.rest.model.NodeList;
import org.mn.dropzone.rest.model.SoftwareVersionData;
import org.mn.dropzone.rest.model.UserAccount;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SdsService {

    String API_PATH = "/api/v4";
    String OAUTH_PATH = "/oauth";

    String AUTHORIZATION_HEADER = "X-Sds-Auth-Token";

    @GET(API_PATH + "/public/software/version")
    Call<SoftwareVersionData> getVersion();

    @GET(API_PATH + "/auth/resources")
    Call<AuthResources> getAuthResources();

    @POST(API_PATH + "/auth/login")
    Call<AuthToken> login(@Body LoginRequest request);

    @GET(API_PATH + "/user/ping")
    Call<Void> checkLogin(@Header(AUTHORIZATION_HEADER) String token);



    @GET(API_PATH + "/nodes")
    Call<NodeList> getNodes(@Header(AUTHORIZATION_HEADER) String token,
                            @Query("parent_id") Long id,
                            @Query("depth_level") Integer depthLevel,
                            @Query("offset") Integer offset,
                            @Query("filter") String filter,
                            @Query("limit") Integer limit);

    @GET(API_PATH + "/nodes/{node_id}")
    Call<Node> getNode(@Header(AUTHORIZATION_HEADER) String token,
                       @Path("node_id") Long nodeId);



    @POST(API_PATH + "/nodes/rooms")
    Call<Node> createRoom(@Header(AUTHORIZATION_HEADER) String token,
                          @Body CreateRoomRequest request);


    @POST(API_PATH + "/nodes/folders")
    Call<Node> createFolder(@Header(AUTHORIZATION_HEADER) String token,
                            @Body CreateFolderRequest request);

    @POST(API_PATH + "/shares/downloads")
    Call<DownloadShare> createDownloadShare(@Header(AUTHORIZATION_HEADER) String token,
                                            @Body CreateDownloadShareRequest request);


    @POST(API_PATH + "/nodes/files/uploads")
    Call<FileUpload> createFileUpload(@Header(AUTHORIZATION_HEADER) String token,
                                      @Body CreateFileUploadRequest request);

    @Multipart
    @POST(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<Void> uploadFile(@Header(AUTHORIZATION_HEADER) String token,
                          @Path("upload_id") String uploadId,
                          @Part("file\"; filename=\"name.extension") RequestBody file);

    @PUT(API_PATH + "/nodes/files/uploads/{upload_id}")
    Call<Node> completeFileUpload(@Header(AUTHORIZATION_HEADER) String token,
                                  @Path("upload_id") String uploadId,
                                  @Body CompleteFileUploadRequest request);


    @GET(API_PATH + "/user/account")
    Call<UserAccount> getUserAccount(@Header(AUTHORIZATION_HEADER) String token);


}