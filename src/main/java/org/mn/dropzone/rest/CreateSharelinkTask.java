package org.mn.dropzone.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.mn.dropzone.eventlistener.SharelinkEvent;
import org.mn.dropzone.eventlistener.SharelinkEventListener;
import org.mn.dropzone.rest.RestClient.Status;
import org.mn.dropzone.rest.model.DownloadShare;
import org.mn.dropzone.util.ConfigIO;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class CreateSharelinkTask implements Runnable {
	private List<File> files;
	private RestClient restClient;
	private ConfigIO cfg;
	private boolean isPasswordProtected;
	private long nodeId;
	private String password;

	public CreateSharelinkTask(long nodeId, boolean isPasswordProtected, String password) {
		this.nodeId = nodeId;
		this.isPasswordProtected = isPasswordProtected;
		this.password = password;
		this.restClient = RestClient.getInstance();
		this.cfg = ConfigIO.getInstance();
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

			// create sharelink
			DownloadShare sharelink = restClient.createSharelink(token, nodeId, isPasswordProtected, password);

			// notify listener
			notifySharelinkEventListener(new SharelinkEvent(this, Status.SUCCESS, sharelink));

		} catch (IOException e) {
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
