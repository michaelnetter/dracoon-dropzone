package org.mn.dropzone.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.mn.dropzone.Constants;
import org.mn.dropzone.NodeType;
import org.mn.dropzone.rest.model.AuthToken;
import org.mn.dropzone.rest.model.CompleteFileUploadRequest;
import org.mn.dropzone.rest.model.CreateDownloadShareRequest;
import org.mn.dropzone.rest.model.CreateFileUploadRequest;
import org.mn.dropzone.rest.model.CreateFolderRequest;
import org.mn.dropzone.rest.model.CreateRoomRequest;
import org.mn.dropzone.rest.model.DownloadShare;
import org.mn.dropzone.rest.model.Expiration;
import org.mn.dropzone.rest.model.FileKeyContainer;
import org.mn.dropzone.rest.model.FileKeyContainerList;
import org.mn.dropzone.rest.model.FileUpload;
import org.mn.dropzone.rest.model.LoginRequest;
import org.mn.dropzone.rest.model.MissingKeys;
import org.mn.dropzone.rest.model.Node;
import org.mn.dropzone.rest.model.NodeList;
import org.mn.dropzone.rest.model.UserAccount;
import org.mn.dropzone.rest.model.UserKeyPairContainer;
import org.mn.dropzone.rest.model.UserPublicKeyContainer;
import org.mn.dropzone.util.ConfigIO;
import org.mn.dropzone.util.TLSSocketFactory;
import org.mn.dropzone.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class RestClient {

	public enum Status {
		SUCCESS, FAILED;
	}

	public final static Logger LOG = LoggerFactory.getLogger(RestClient.class);
	private static final String DEFAULT_STORAGE_PATH = "/Dropzone_Share";
	private static final String RESOLUTION_STRATEGY = "autorename";

	private static RestClient instance;
	private SdsService sdsService;
	private OkHttpClient mOkHttpClient;
	private Retrofit mRetrofit;
	private Gson mGson;
	private ConfigIO cfg;

	private RestClient() {
		initConfig();
	}

	/**
	 * Sds login using parameters, returns true if successful
	 * 
	 * @param username
	 * @param pwd
	 * @param authMethod
	 * @return
	 * @throws IOException
	 */
	public boolean login(String username, String pwd, String authMethod) throws IOException {

		// Create login request
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.login = username;
		loginRequest.password = pwd;
		loginRequest.authType = authMethod;
		loginRequest.language = Locale.getDefault().getLanguage();

		return loginInternal(loginRequest);
	}

	/**
	 * Sds login using config file, returns true if successful
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean login() throws IOException {
		// Create login request
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.login = cfg.getUsername();
		loginRequest.password = cfg.getPassword();
		loginRequest.authType = cfg.getAuthMethod();
		loginRequest.language = Locale.getDefault().getLanguage();

		return loginInternal(loginRequest);
	}

	/**
	 * Executes sds login, returns true if successful, else returns false
	 * 
	 * @param loginRequest
	 * @return
	 * @throws IOException
	 */
	private boolean loginInternal(LoginRequest loginRequest) throws IOException {
		Call<AuthToken> login = sdsService.login(loginRequest);
		Response<AuthToken> response;

		response = (Response<AuthToken>) login.execute();

		// If an error occurred: Abort
		if (!response.isSuccessful()) {
			return false;
		} else {
			// save auth token in config object
			cfg.setAuthToken(response.body().token);
			return true;
		}
	}

	/**
	 * Get the roomId of the current path
	 * 
	 * @param token
	 * @param nodeId
	 * @return
	 * @throws IOException
	 */
	public long getParentRoomId(String token, long nodeId) throws IOException {
		Call<Node> nodeCall = sdsService.getNode(token, nodeId);

		Response<Node> response = (Response<Node>) nodeCall.execute();

		// If an error occurred: Abort
		if (!response.isSuccessful()) {
			return -1;
		} else {
			// save auth token in config object
			if (response.body().type == NodeType.ROOM) {
				return nodeId;
			} else {
				return getParentRoomId(token, response.body().parentId);
			}
		}
	}

	/**
	 * Returns nodeId for the given storage path
	 * 
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public long getStoragePathId(String token) throws UnsupportedEncodingException, IOException {
		long nodeId = 0;
		String storagePath = cfg.getStoragePath();

		if (storagePath == null || storagePath.isEmpty()) {
			storagePath = DEFAULT_STORAGE_PATH;
		}

		if (storagePath.startsWith("/")) {
			storagePath = storagePath.substring(1);
		}

		if (storagePath.endsWith("/")) {
			storagePath = storagePath.substring(0, storagePath.length() - 1);
		}

		String[] path = storagePath.split("/");
		for (String nodeName : path) {
			Node[] nodeList = getNodesByName(token, nodeName, nodeId, 0);
			if (nodeList == null) {
				return -1;
			}

			// search for nodes with nodeName on given level
			if (nodeList.length > 0) {
				// if more than room with that name exists
				for (Node node : nodeList) {
					if (node.name.equals(nodeName)) {
						nodeId = node.id;
						break;
					}
				}
			} else {
				Node node = null;
				// Create Room
				if (nodeId == 0) {
					node = createRoom(token, nodeName);
					if (node == null) {
						return -1;
					}
				}
				// Create Folder
				else {
					node = createFolder(token, nodeName, nodeId);
					if (node == null) {
						return -1;
					}
				}
				nodeId = node.id;
			}
		}
		return nodeId;
	}

	/**
	 * Get the current user account for the given token
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public UserAccount getUserAccount(String token) throws IOException {
		UserAccount account;
		Call<UserAccount> call = sdsService.getUserAccount(token);

		Response<UserAccount> response = call.execute();
		account = response.body();

		return account;
	}

	/**
	 * Get missing FileKeys for given fileId
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public MissingKeys getMissingFileKeys(String token, long fileId) throws IOException {
		MissingKeys missingKeys;
		Call<MissingKeys> call = sdsService.getMissingFileKeys(token, null, fileId, null, null, null, 3);

		Response<MissingKeys> response = call.execute();
		missingKeys = response.body();

		return missingKeys;
	}

	/**
	 * Create room with the given name
	 * 
	 * @param token
	 * @param roomName
	 * @return
	 * @throws IOException
	 */
	public Node createRoom(String token, String roomName) throws IOException {
		Node node = null;

		UserAccount account = getUserAccount(token);
		if (account == null) {
			return null;
		}

		CreateRoomRequest crr = new CreateRoomRequest();
		crr.name = roomName;
		crr.adminIds = new Long[] { account.id };

		Call<Node> call = sdsService.createRoom(token, crr);

		Response<Node> response = call.execute();
		node = response.body();

		return node;
	}

	/**
	 * Create room with the given name
	 * 
	 * @param token
	 * @param roomName
	 * @return
	 * @throws IOException
	 */
	public Node createFolder(String token, String folderName, long parentId) throws IOException {
		Node node = null;
		CreateFolderRequest cfr = new CreateFolderRequest();
		cfr.name = folderName;
		cfr.parentId = parentId;

		Call<Node> call = sdsService.createFolder(token, cfr);

		Response<Node> response = call.execute();
		node = response.body();

		return node;
	}

	/**
	 * Create a new file upload channel
	 * 
	 * @param token
	 * @param parentId
	 * @param fileName
	 * @param fileSize
	 * @param classification
	 * @return
	 * @throws IOException
	 */
	public FileUpload createUploadChannel(String token, long parentId, String fileName, long fileSize,
			int classification, boolean isSetExpiration) throws IOException {
		CreateFileUploadRequest uploadRequest = new CreateFileUploadRequest();
		uploadRequest.parentId = parentId;
		uploadRequest.name = fileName;
		uploadRequest.size = fileSize;
		uploadRequest.classification = classification;

		if (isSetExpiration) {
			Expiration expiration = new Expiration();

			expiration.enableExpiration = isSetExpiration;
			expiration.expireAt = Util.formatExpirationDate(Util.getDaysFromNow(Constants.EXPIRATION_PERIOD));
			uploadRequest.expiration = expiration;
		}

		Call<FileUpload> call = sdsService.createFileUpload(token, uploadRequest);
		Response<FileUpload> response = call.execute();
		return response.body();
	}

	/**
	 * Upload a file
	 * 
	 * @param token
	 * @param uploadId
	 * @param file
	 * @throws IOException
	 */
	public void uploadFile(String token, String uploadId, File file) throws IOException {

		// RequestBody requestBody = new
		// MultipartBody.Builder().setType(MultipartBody.FORM)
		// .addFormDataPart(file.getAbsolutePath(), file.getName(),
		// RequestBody.create(MediaType.parse("application/octet-stream"),
		// file))
		// .build();

		MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
				RequestBody.create(MediaType.parse("application/octet-stream"), file));

		Call<Void> call = sdsService.uploadFile(token, uploadId, filePart);
		call.execute();
	}

	/**
	 * Complete upload
	 * 
	 * @param token
	 * @param uploadId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Node completeUpload(String token, String uploadId, File file, FileKeyContainer fileKey) throws IOException {
		CompleteFileUploadRequest cfur = new CompleteFileUploadRequest();
		cfur.fileName = file.getName();
		cfur.resolutionStrategy = RESOLUTION_STRATEGY;
		if (fileKey != null) {
			cfur.fileKey = fileKey;
		}

		Call<Node> call = sdsService.completeFileUpload(token, uploadId, cfur);
		Response<Node> response = call.execute();
		return response.body();
	}

	/**
	 * Creates a sharelink for the given nodeid
	 * 
	 * @param token
	 * @param nodeId
	 * @param passwordProtected
	 * @param password
	 * @param isSetExpiration
	 * @param fileKeyContainer
	 * @return <{@link DownloadShare}
	 * @throws IOException
	 */
	public DownloadShare createSharelink(String token, long nodeId, boolean passwordProtected, String password,
			boolean isSetExpiration, FileKeyContainer fileKeyContainer, UserKeyPairContainer keyPair) throws IOException {
		CreateDownloadShareRequest request = new CreateDownloadShareRequest();
		request.nodeId = nodeId;
		request.notifyCreator = false;
		request.sendMail = false;

		// if sharelink is in an encrypted room
		if (fileKeyContainer != null) {
			request.fileKey = fileKeyContainer;
			request.keyPair = keyPair;
		} else {
			if (passwordProtected) {
				request.password = password;
			}
		}
		
		if (isSetExpiration) {
			Expiration expiration = new Expiration();

			expiration.enableExpiration = isSetExpiration;
			expiration.expireAt = Util.formatExpirationDate(Util.getDaysFromNow(Constants.EXPIRATION_PERIOD));
			request.expiration = expiration;
		}

		Call<DownloadShare> call = sdsService.createDownloadShare(token, request);
		Response<DownloadShare> response = call.execute();

		return response.body();
	}

	/**
	 * Find node by name
	 * 
	 * @param token
	 * @param name
	 * @param parentId
	 * @param depthLevel
	 * @return
	 * @throws IOException
	 */
	public Node[] getNodesByName(String token, String name, long parentId, int depthLevel)
			throws IOException, UnsupportedEncodingException {
		Node[] nodeList = null;

		name = URLEncoder.encode(name, "UTF-8");

		Call<NodeList> call = sdsService.getNodes(token, parentId, depthLevel, null, "name:cn:" + name, null);

		Response<NodeList> response = call.execute();
		nodeList = response.body().items;

		return nodeList;
	}

	/**
	 * Returns true if the room of the given nodeId is encrypted
	 * 
	 * @param token
	 * @param nodeId
	 * @return boolean true if room is encrypted
	 * @throws IOException
	 */
	public boolean isEncryptedRoom(String token, long nodeId) throws IOException {
		Call<Node> node = sdsService.getNode(token, nodeId);
		Response<Node> response = node.execute();
		return response.body().isEncrypted;
	}

	/**
	 * Get the public key of the current user
	 */
	public UserPublicKeyContainer getUserPublicKey(String token) throws IOException {

		Call<UserKeyPairContainer> call = sdsService.getUserKeyPair(token);
		Response<UserKeyPairContainer> response = call.execute();
		UserPublicKeyContainer publicKeyContainer = response.body().publicKeyContainer;
		return publicKeyContainer;
	}

	/**
	 * Return singleton instance of <{@link RestClient}
	 * 
	 * @return <{@link RestClient}
	 */
	public static synchronized RestClient getInstance() {
		if (RestClient.instance == null) {
			RestClient.instance = new RestClient();
		}
		return RestClient.instance;
	}

	/**
	 * Init REST libraries
	 */
	private void initConfig() {
		// Init config
		initConfigIO();

		// Init OkHttp
		initOkHttp();
		// Init Gson
		initGson();
		// Init Retrofit
		initRetrofit();
		// Init Sds Service
		initSdsService();
	}

	/**
	 * Get ConfigIO instance
	 */
	private void initConfigIO() {
		cfg = ConfigIO.getInstance();
	}

	/**
	 * Create {@link SdsService} instance
	 */
	private void initSdsService() {
		sdsService = mRetrofit.create(SdsService.class);
	}

	/**
	 * Create {@link Retrofit} instance
	 */
	private void initRetrofit() {
		LOG.debug("Init Retrofit.");
		String serverUrl = ConfigIO.getInstance().getServerUrl();
		mRetrofit = createRetrofit(serverUrl);
	}

	/**
	 * Create {@link Gson} instance
	 */
	private void initGson() {
		LOG.debug("Init Gson.");

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Void.class, new JsonDeserializer<Void>() {
			@Override
			public Void deserialize(JsonElement json, Type type, JsonDeserializationContext context)
					throws JsonParseException {
				return null;
			}
		});
		mGson = gsonBuilder.create();
	}

	/**
	 * Create {@link Retrofit} instance
	 */
	private Retrofit createRetrofit(String serverUrl) {
		return new Retrofit.Builder().baseUrl(serverUrl).client(mOkHttpClient)
				.addConverterFactory(GsonConverterFactory.create(mGson)).build();
	}

	/**
	 * Create {@link OkHttpClient} instance
	 */
	private void initOkHttp() {
		LOG.debug("Init OkHttp.");

		// Get SSL socket factory and X509 trust manager
		SSLSocketFactory socketFactory = getSSLSocketFactory();
		X509TrustManager trustManager = getX509TrustManager();

		// Init OkHttp
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		if (socketFactory != null && trustManager != null) {
			builder.sslSocketFactory(socketFactory, trustManager);
		}

		builder.connectTimeout(15, TimeUnit.SECONDS);
		builder.readTimeout(15, TimeUnit.SECONDS);
		builder.writeTimeout(15, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(true);
		mOkHttpClient = builder.build();
	}

	// TODO: Throw exception (Find a solution which prevents the start of the
	// app.)
	private X509TrustManager getX509TrustManager() {
		try {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
				return (X509TrustManager) trustManagers[0];
			} else {
				LOG.error(String.format("Error while retrieving X509 trust manager! " + "(TrustMangers: %s)",
						Arrays.toString(trustManagers)));
				return null;
			}
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			LOG.error("Error while retrieving X509 trust manager!", e);
			return null;
		}
	}

	// TODO: Throw exception (Find a solution which prevents the start of the
	// app.)
	private SSLSocketFactory getSSLSocketFactory() {
		try {
			return new TLSSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			LOG.error("Error while creating SSL socket factory!", e);
			return null;
		}
	}

}