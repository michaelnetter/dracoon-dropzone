package org.mn.dropzone.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.controlsfx.control.PopOver.ArrowLocation;
import org.jnativehook.mouse.NativeMouseEvent;
import org.mn.dropzone.Constants;
import org.mn.dropzone.Constants.OSType;
import org.mn.dropzone.i18n.I18n;
import org.mn.dropzone.model.AuthModel;
import org.mn.dropzone.model.AuthModel.Type;
import org.mn.dropzone.model.ScreenModel;
import org.mn.dropzone.model.ScreenPosition;
import org.mn.dropzone.model.ScreenPosition.Pos;
import org.mn.dropzone.popover.DropzonePopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.PopupWindow.AnchorLocation;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class Util {
	public final static Logger LOG = LoggerFactory.getLogger(Util.class);

	private static final int X_Y_EXPAND = 100;
	private static final int ARROW_SIZE = 20;
	private static final int PADDING = 120;

	/**
	 * Determine file extension
	 * 
	 * @param name
	 * @return
	 */
	public static String getExtensionFromName(String name) {
		if (name == null) {
			return null;
		}

		int dotIndex = name.lastIndexOf(".");
		if (dotIndex > 0 && dotIndex < name.length() - 1) {
			return name.substring(dotIndex + 1).toLowerCase();
		} else {
			return null;
		}
	}

	/**
	 * Try to load native look and feel
	 */
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOG.error("Could not load native L&F");
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException ex) {
				LOG.error("Could not load crossplatform L&F");
			}
		}
	}

	/**
	 * Returns the position of the upper left corner of the popover winndow
	 * 
	 * @param pos
	 * @return
	 */
	public static Point getPopOverPosition() {

		ConfigIO cfg = ConfigIO.getInstance();

		String screenId = cfg.getScreenId();
		ScreenModel screenModel = getScreen(screenId);
		int screenWidth = screenModel.getWidth();
		int screenHeight = screenModel.getHeight();
		Point point = screenModel.getTopLeftCorner();

		int scrPosId = cfg.getScreenPositionId();
		ScreenPosition screenPos = getScreenPosition(scrPosId);
		Pos pos = screenPos.getPos();

		switch (pos) {
		case TOP_LEFT:
			point.setLocation(point.x + PADDING, point.y + 0 + ARROW_SIZE);
			break;

		case TOP_RIGHT:
			point.setLocation(point.x + screenWidth + PADDING / 2, point.y + 0 + ARROW_SIZE);
			break;

		case BOTTOM_LEFT:
			point.setLocation(point.x + PADDING, point.y + screenHeight + DropzonePopOver.HEIGHT - ARROW_SIZE * 2);
			break;

		case BOTTOM_RIGHT:
			point.setLocation(point.x + screenWidth + PADDING / 2,
					point.y + screenHeight + DropzonePopOver.HEIGHT - ARROW_SIZE * 2);
			break;
		}
		return point;
	}

	/**
	 * Returns the position of the upper left corner of the popover winndow
	 * 
	 * @param pos
	 * @return
	 */
	public static ArrowLocation getArrowLocation() {
		ConfigIO cfg = ConfigIO.getInstance();
		cfg.getScreenPositionId();
		int scrPosId = cfg.getScreenPositionId();
		ScreenPosition screenPos = getScreenPosition(scrPosId);
		Pos pos = screenPos.getPos();

		switch (pos) {
		case TOP_LEFT:
			return ArrowLocation.TOP_CENTER;

		case TOP_RIGHT:
			return ArrowLocation.TOP_CENTER;

		case BOTTOM_LEFT:
			return ArrowLocation.BOTTOM_CENTER;

		case BOTTOM_RIGHT:
			return ArrowLocation.BOTTOM_CENTER;
		default:
			return ArrowLocation.BOTTOM_CENTER;
		}
	}

	/**
	 * Returns the position of the upper left corner of the popover winndow
	 * 
	 * @param pos
	 * @return
	 */
	public static AnchorLocation getAnchorLocation() {
		ConfigIO cfg = ConfigIO.getInstance();
		cfg.getScreenPositionId();
		int scrPosId = cfg.getScreenPositionId();
		ScreenPosition screenPos = getScreenPosition(scrPosId);
		Pos pos = screenPos.getPos();

		switch (pos) {
		case TOP_LEFT:
			return AnchorLocation.CONTENT_TOP_LEFT;

		case TOP_RIGHT:
			return AnchorLocation.CONTENT_TOP_RIGHT;

		case BOTTOM_LEFT:
			return AnchorLocation.CONTENT_BOTTOM_LEFT;

		case BOTTOM_RIGHT:
			return AnchorLocation.CONTENT_BOTTOM_RIGHT;
		default:
			return AnchorLocation.CONTENT_TOP_LEFT;
		}
	}

	/**
	 * Returns true if mouse cursor is in activation zone, i.e. the zone which
	 * triggers showing the popup
	 * 
	 * @param event
	 * @param pos
	 * @return
	 */
	public static boolean isInActivationZone(NativeMouseEvent event) {

		ConfigIO cfg = ConfigIO.getInstance();
		String screenId = cfg.getScreenId();
		ScreenModel screenModel = getScreen(screenId);
		int screenWidth = screenModel.getWidth();
		int screenHeight = screenModel.getHeight();
		Point topLeftCorner = screenModel.getTopLeftCorner();

		int scrPosId = cfg.getScreenPositionId();
		ScreenPosition screenPos = getScreenPosition(scrPosId);
		Pos pos = screenPos.getPos();

		int min_x = topLeftCorner.x;
		int max_x = topLeftCorner.x;
		int min_y = topLeftCorner.y;
		int max_y = topLeftCorner.y;
		int x = event.getX();
		int y = event.getY();

		switch (pos) {
		case TOP_LEFT:
			min_x = min_x + 0;
			min_y = min_y + 0;
			max_x = max_x + X_Y_EXPAND;
			max_y = max_y + X_Y_EXPAND;
			break;

		case TOP_RIGHT:
			min_x = min_x + screenWidth - X_Y_EXPAND;
			min_y = min_y + 0;
			max_x = max_x + screenWidth;
			max_y = max_y + X_Y_EXPAND;
			break;

		case BOTTOM_LEFT:
			min_x = min_x + 0;
			min_y = min_y + screenHeight - X_Y_EXPAND;
			max_x = max_x + X_Y_EXPAND;
			max_y = max_y + screenHeight;
			break;

		case BOTTOM_RIGHT:
			min_x = min_x + screenWidth - X_Y_EXPAND;
			min_y = min_y + screenHeight - X_Y_EXPAND;
			max_x = max_x + screenWidth;
			max_y = max_y + screenHeight;
			break;
		}
		if ((x > min_x && x < max_x) && (y > min_y && y < max_y)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the screen for the given screen id. If no screen is found, the
	 * default screen is returned
	 * 
	 * @return
	 */
	public static ScreenModel getScreen(String idString) {
		ScreenModel screen = null;
		ScreenModel[] screens = getScreens();
		for (ScreenModel scr : screens) {
			if (scr.getIdString().equals(idString)) {
				screen = scr;
				break;
			}
		}
		if (screen == null) {
			return (getScreen(getDefaultScreenId()));
		}
		return screen;
	}

	/**
	 * Returns the unique id of the default screenid
	 * 
	 * @return
	 */
	public static String getDefaultScreenId() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getIDstring();
	}

	/**
	 * Returns an array of screens that are available on the current system
	 * 
	 * @return
	 */
	public static ScreenModel[] getScreens() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		ScreenModel[] models = new ScreenModel[gd.length];
		for (int i = 0; i < gd.length; i++) {
			if (gd[i].getIDstring().equals(getDefaultScreenId())) {
				models[i] = new ScreenModel(gd[i], "(" + I18n.get("settings.mainscreen") + ")", i + 1);
			} else {
				models[i] = new ScreenModel(gd[i], "", i + 1);
			}

		}

		return models;
	}

	/**
	 * Returns the screen for the given screen id. If no screen is found, the
	 * default screen is returned
	 * 
	 * @return
	 */
	public static ScreenPosition getScreenPosition(int id) {
		ScreenPosition position = null;
		ScreenPosition[] positions = getScreenPositions();
		for (ScreenPosition p : positions) {
			if (p.getPos().getId() == id) {
				position = p;
				break;
			}
		}
		if (position == null) {
			return (getScreenPosition(getDefaultPositionId()));
		}
		return position;
	}

	/**
	 * Returns the auth model for a given auth type
	 * 
	 * @return
	 */
	public static AuthModel getAuthModel(String id) {
		AuthModel model = null;
		AuthModel[] authMethods = getAuthMethods();
		for (AuthModel auth : authMethods) {
			if (auth.getType().getId().equals(id)) {
				model = auth;
				break;
			}
		}
		if (model == null) {
			return new AuthModel(Type.E_MAIL, I18n.get("settings.authmethod.email"));
		}
		return model;
	}

	/**
	 * Returns the id of Pos.BOTTOM_RIGHT which is defined as default position
	 * 
	 * @return
	 */
	public static int getDefaultPositionId() {
		return Pos.BOTTOM_RIGHT.getId();
	}

	/**
	 * Returns an array of all screen positions
	 * 
	 * @return
	 */
	public static ScreenPosition[] getScreenPositions() {
		ScreenPosition[] positions = new ScreenPosition[4];
		positions[0] = new ScreenPosition(Pos.TOP_LEFT, I18n.get("settings.position.topleft"));
		positions[1] = new ScreenPosition(Pos.TOP_RIGHT, I18n.get("settings.position.topright"));
		positions[2] = new ScreenPosition(Pos.BOTTOM_LEFT, I18n.get("settings.position.bottomleft"));
		positions[3] = new ScreenPosition(Pos.BOTTOM_RIGHT, I18n.get("settings.position.bottomright"));
		return positions;
	}

	/**
	 * Returns an array of all auth methods
	 * 
	 * @return
	 */
	public static AuthModel[] getAuthMethods() {
		AuthModel[] authModels = new AuthModel[3];
		authModels[0] = new AuthModel(AuthModel.Type.ACTIVE_DIRECTORY, I18n.get("settings.authmethod.ad"));
		authModels[1] = new AuthModel(AuthModel.Type.E_MAIL, I18n.get("settings.authmethod.email"));
		authModels[2] = new AuthModel(AuthModel.Type.RADIUS, I18n.get("settings.authmethod.radius"));

		return authModels;
	}

	/**
	 * Returns the current operating system type
	 * 
	 * @return
	 */
	public static OSType getOSType() {
		OSType type = null;
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN")) {
			type = OSType.WINDOWS;
		} else if (OS.contains("MAC")) {
			type = OSType.MACOS;
		} else if (OS.contains("NUX")) {
			type = OSType.UNIX;
		} else {
			type = OSType.OTHER;
		}
		return type;
	}

	/**
	 * Adds the given number of days to today and returns the new date
	 * 
	 * @param days
	 * @return
	 */
	public static Date getDaysFromNow(int days) {
		LocalDate localDate = LocalDate.now().plus(days, ChronoUnit.DAYS);
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return date;

	}
	
	/**
	 * Returns the date in the format(2015-12-31T23:59:00)
	 * @param date
	 * @return
	 */
	public static String formatExpirationDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.format(date);
	}
}
