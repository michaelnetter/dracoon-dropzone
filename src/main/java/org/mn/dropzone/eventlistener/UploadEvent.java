package org.mn.dropzone.eventlistener;

import java.util.EventObject;

import org.mn.dropzone.crypto.model.PlainFileKey;
import org.mn.dropzone.rest.RestClient.Status;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class UploadEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private Status status;
	private long nodeId;
	private boolean isPasswordProtected, isSetExpiration, isEncryptedRoom;

	private PlainFileKey plainFileKey;

	public UploadEvent(Object source, Status status, long nodeId, boolean isPasswordProtected, boolean isSetExpiration,
			boolean isEncryptedRoom, PlainFileKey plainFileKey) {
		super(source);
		this.status = status;
		this.nodeId = nodeId;
		this.isPasswordProtected = isPasswordProtected;
		this.isSetExpiration = isSetExpiration;
		this.isEncryptedRoom = isEncryptedRoom;
		this.plainFileKey = plainFileKey;
	}

	public Status getStatus() {
		return status;
	}

	public long getNodeId() {
		return nodeId;
	}

	public boolean isPasswordProtected() {
		return isPasswordProtected;
	}

	public boolean isSetExpiration() {
		return isSetExpiration;
	}

	public boolean isEncryptedRoom() {
		return isEncryptedRoom;
	}

	public PlainFileKey getPlainFileKey() {
		return plainFileKey;
	}

}
