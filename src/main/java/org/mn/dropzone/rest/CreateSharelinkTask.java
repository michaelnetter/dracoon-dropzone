package org.mn.dropzone.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.swing.event.EventListenerList;

import org.mn.dropzone.crypto.Crypto;
import org.mn.dropzone.crypto.CryptoException;
import org.mn.dropzone.crypto.model.PlainFileKey;
import org.mn.dropzone.crypto.model.UserKeyPair;
import org.mn.dropzone.eventlistener.SharelinkEvent;
import org.mn.dropzone.eventlistener.SharelinkEventListener;
import org.mn.dropzone.rest.RestClient.Status;
import org.mn.dropzone.rest.model.DownloadShare;
import org.mn.dropzone.rest.model.FileKeyContainer;
import org.mn.dropzone.rest.model.UserKeyPairContainer;
import org.mn.dropzone.rest.model.UserPrivateKeyContainer;
import org.mn.dropzone.rest.model.UserPublicKeyContainer;
import org.mn.dropzone.util.ConfigIO;
import org.mn.dropzone.util.CryptoUtil;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class CreateSharelinkTask implements Runnable {
	private RestClient restClient;
	private ConfigIO cfg;
	private long nodeId;
	private String password;
	private boolean isPasswordProtected, isSetExpiration;
	private PlainFileKey plainFileKey;

	public CreateSharelinkTask(long nodeId, boolean isPasswordProtected, String password, boolean isSetExpiration,
			PlainFileKey plainFileKey) {
		this.nodeId = nodeId;
		this.isPasswordProtected = isPasswordProtected;
		this.isSetExpiration = isSetExpiration;
		this.password = password;
		this.restClient = RestClient.getInstance();
		this.cfg = ConfigIO.getInstance();
		this.plainFileKey = plainFileKey;
	}

	@Override
	public void run() {
		try {
			// login
			boolean loginSuccessful = restClient.login();
			if (!loginSuccessful) {
				notifySharelinkEventListener(new SharelinkEvent(this, Status.FAILED, null));
				return;
			}
			String token = cfg.getAuthToken();

			// get storage path nodeId
			long storagePathId = restClient.getStoragePathId(token);

			// check if room is encrypted
			boolean isEncryptedRoom = restClient.isEncryptedRoom(token, storagePathId);

			DownloadShare sharelink = null;
			if (isEncryptedRoom) {
				// generate new keypair for user
				try {
					int maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("RC5");
					System.out.println(maxAllowedKeyLength);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UserKeyPair userKeyPair = Crypto.generateUserKeyPair(password);

				UserPublicKeyContainer userPublicKeyContainer = new UserPublicKeyContainer();
				userPublicKeyContainer.publicKey = userKeyPair.getUserPublicKey().getPublicKey();
				userPublicKeyContainer.version = userKeyPair.getUserPublicKey().getVersion();

				UserPrivateKeyContainer userPrivateKeyContainer = new UserPrivateKeyContainer();
				userPrivateKeyContainer.privateKey = userKeyPair.getUserPrivateKey().getPrivateKey();
				userPrivateKeyContainer.version = userKeyPair.getUserPrivateKey().getVersion();

				UserKeyPairContainer userKeyPairContainer = new UserKeyPairContainer();
				userKeyPairContainer.publicKeyContainer = userPublicKeyContainer;
				userKeyPairContainer.privateKeyContainer =userPrivateKeyContainer;

				FileKeyContainer fileKeyContainer = CryptoUtil.encryptFileKey(plainFileKey, userPublicKeyContainer);

				// create sharelink
				sharelink = restClient.createSharelink(token, nodeId, isPasswordProtected, password, isSetExpiration,
						fileKeyContainer, userKeyPairContainer);

			} else {
				// create sharelink
				sharelink = restClient.createSharelink(token, nodeId, isPasswordProtected, password, isSetExpiration,
						null, null);
			}

			// notify listener
			notifySharelinkEventListener(new SharelinkEvent(this, Status.SUCCESS, sharelink));

		} catch (IOException | CryptoException e) {
			notifySharelinkEventListener(new SharelinkEvent(this, Status.FAILED, null));
		}
	}

	/*
	 * Listeners #############################################################
	 */
	private EventListenerList listeners = new EventListenerList();

	public void addSharelinkEventListener(SharelinkEventListener listener) {
		listeners.add(SharelinkEventListener.class, listener);
	}

	/**
	 * Notify Listeners of a date data change
	 * 
	 * @param event
	 */
	protected synchronized void notifySharelinkEventListener(SharelinkEvent event) {
		for (SharelinkEventListener l : listeners.getListeners(SharelinkEventListener.class)) {
			l.handleSharelinkEvent(event);
		}
	}
}
