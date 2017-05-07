package org.mn.dropzone.model;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;

import org.mn.dropzone.i18n.I18n;

/**
 * Model for screens for internal use
 * 
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class ScreenModel {
	private int width, height;
	private Point topLeftCorner;
	private String name, idString;

	public ScreenModel(GraphicsDevice device, String name, int number) {
		this.name = number + " " + name;
		this.idString = device.getIDstring();
		this.width = device.getDisplayMode().getWidth();
		this.height = device.getDisplayMode().getHeight();

		GraphicsConfiguration[] gc = device.getConfigurations();
		for (GraphicsConfiguration curGc : gc) {
			Rectangle bounds = curGc.getBounds();
			this.topLeftCorner = new Point((int) bounds.getX(), (int) bounds.getY());
		}

	}

	public String toString() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Point getTopLeftCorner() {
		return topLeftCorner;
	}

	public String getName() {
		return name + " " + idString;
	}

	public String getIdString() {
		return idString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idString == null) ? 0 : idString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScreenModel other = (ScreenModel) obj;
		if (idString == null) {
			if (other.idString != null)
				return false;
		} else if (!idString.equals(other.idString))
			return false;
		return true;
	}

}
