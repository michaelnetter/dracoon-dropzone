package org.mn.dropzone.rest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.mn.dropzone.crypto.Crypto;
import org.mn.dropzone.crypto.CryptoSystemException;
import org.mn.dropzone.crypto.InvalidFileKeyException;
import org.mn.dropzone.crypto.InvalidKeyPairException;
import org.mn.dropzone.crypto.model.EncryptedFileKey;
import org.mn.dropzone.crypto.model.PlainFileKey;
import org.mn.dropzone.crypto.model.UserPublicKey;
import org.mn.dropzone.eventlistener.UploadEvent;
import org.mn.dropzone.eventlistener.UploadEventListener;
import org.mn.dropzone.rest.RestClient.Status;
import org.mn.dropzone.rest.model.FileKeyContainer;
import org.mn.dropzone.rest.model.FileUpload;
import org.mn.dropzone.rest.model.MissingKeys;
import org.mn.dropzone.rest.model.Node;
import org.mn.dropzone.rest.model.UserPublicKeyContainer;
import org.mn.dropzone.util.ConfigIO;
import org.mn.dropzone.util.CryptoUtil;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class FileUploadTask implements Runnable {
	private List<File> files;
	private RestClient restClient;
	private ConfigIO cfg;
	private boolean isPwdProtected, isSetExpiration;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yy-MM-dd HH-mm-SS");

	public FileUploadTask(List<File> files, boolean isPasswordProtected, boolean isSetExpiration) {
		this.files = files;
		this.isPwdProtected = isPasswordProtected;
		this.isSetExpiration = isSetExpiration;
		this.restClient = RestClient.getInstance();
		this.cfg = ConfigIO.getInstance();
	}

	@Override
	public void run() {
		long nodeId = -1;
		boolean isEncryptedRoom = false;
		PlainFileKey fileKey = null;
		try {
			// login
			boolean loginSuccessful = restClient.login();
			if (!loginSuccessful) {
				notifyUploadEventListener(
						new UploadEvent(this, Status.FAILED, -1, isPwdProtected, isSetExpiration, false, null));
				return;
			}
			String token = cfg.getAuthToken();

			// get storage path nodeId
			long storagePathId = restClient.getStoragePathId(token);

			// check if room is encrypted
			isEncryptedRoom = restClient.isEncryptedRoom(token, storagePathId);

			// create folder if multiple files are uploaded
			if (files.size() > 1) {
				storagePathId = restClient.createFolder(token, dateFormatter.format(new Date()), storagePathId).id;
			}

			// for each file
			for (File file : files) {
				// open upload channel
				FileUpload ulChannel = restClient.createUploadChannel(token, storagePathId, file.getName(),
						file.length(), 1, isSetExpiration);

				// encrypt files first if room is encrypted
				FileKeyContainer fileKeyContainer = null;

				if (isEncryptedRoom) {
					// get public key from user
					// get user fileKey
					UserPublicKeyContainer userPublicKeyContainer = restClient.getUserPublicKey(token);
					if (userPublicKeyContainer == null) {
						throw new IOException();
					}

					// Generate plain file key
					fileKey = Crypto.generateFileKey();
					file = CryptoUtil.encryptFile(file, fileKey);
					fileKeyContainer = CryptoUtil.encryptFileKey(fileKey, userPublicKeyContainer);
				}

				// upload file
				restClient.uploadFile(token, ulChannel.uploadId, file);

				// complete upload
				Node node = restClient.completeUpload(token, ulChannel.uploadId, file, fileKeyContainer);
				nodeId = node.id;
			}

			// set nodeId to parent folder if more than one file was uploaded
			if (files.size() > 1) {
				nodeId = storagePathId;
			}

			// notify listener
			notifyUploadEventListener(new UploadEvent(this, Status.SUCCESS, nodeId, isPwdProtected, isSetExpiration,
					isEncryptedRoom, fileKey));

		} catch (IOException e) {
			notifyUploadEventListener(
					new UploadEvent(this, Status.FAILED, -1, isPwdProtected, isSetExpiration, isEncryptedRoom, null));
		}
	}

	/*
	 * Listeners #############################################################
	 */
	private EventListenerList listeners = new EventListenerList();

	public void addUploadEventListener(UploadEventListener listener) {
		listeners.add(UploadEventListener.class, listener);
	}

	/**
	 * Notify Listeners of a date data change
	 * 
	 * @param event
	 */
	protected synchronized void notifyUploadEventListener(UploadEvent event) {
		for (UploadEventListener l : listeners.getListeners(UploadEventListener.class)) {
			l.handleUploadEvent(event);
		}
	}
}
