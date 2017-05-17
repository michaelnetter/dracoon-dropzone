package org.mn.dropzone.popover;

import java.awt.Point;
import java.util.logging.Handler;

import org.controlsfx.control.PopOver;
import org.mn.dropzone.eventlistener.DropzoneDragEvent;
import org.mn.dropzone.i18n.I18n;
import org.mn.dropzone.util.Util;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class DropzonePopOverUI extends PopOver {

	private DropzonePopOver popOver;

	private static final int ANIMATION_TIME = 100; // in ms

	public DropzonePopOverUI(DropzonePopOver popOver) {
		this.popOver = popOver;
		disableJNativeHookLoggers();
		initUI();

		addMouseDragEventListeners();
	}

	/**
	 * Set default behaviour of PopOver
	 */
	private void initUI() {
		setDetachable(false);
		setDetached(false);
		setTitle(I18n.get("tray.appname"));
		setAnimated(true);
		setAutoFix(false);

		setFadeInDuration(new Duration(ANIMATION_TIME));
		setAutoFix(true);
		setHeaderAlwaysVisible(true);
		setOpacity(DropzonePopOver.POPOVER_OPACITY);
		arrowSizeProperty().bind(new SimpleDoubleProperty(10));
		arrowIndentProperty().bind(new SimpleDoubleProperty(40));
		cornerRadiusProperty().bind(new SimpleDoubleProperty(0));
	}

	/**
	 * Returns a reference to this instance
	 * 
	 * @return
	 */
	private DropzonePopOverUI getPopOverUI() {
		return this;
	}

	/**
	 * Hide popover UI
	 */
	public void hidePopOver() {
		// hide panel
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (isShowing()) {
					hide(new Duration(ANIMATION_TIME));
				}
			}
		});
	}

	/**
	 * Show popover UI
	 * 
	 * @param hovered
	 */
	public void showPopOver(boolean hovered) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				JFXPanel fxPanel = getPopOverUI().getDropzonePopover().getJFXPanel();
				Parent root = fxPanel.getScene().getRoot();

				Point pos = Util.getPopOverPosition();
				int x = pos.x;
				int y = pos.y;

				setArrowLocation(Util.getArrowLocation());
				setAnchorLocation(Util.getAnchorLocation());

				if (!isShowing()) {
					show(root, x, y);
					applyStyle();
					show(root, x, y);
					setContentNode(createPopoverContent(hovered));
				}
			}
		});
	}

	/**
	 * Display a message using popover for a given timeperiod
	 * 
	 * @param msg
	 * @param icon
	 * @param textColor
	 */
	public void showPopOver(String msg, ImageView icon, Color textColor, int textSize) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				JFXPanel fxPanel = getPopOverUI().getDropzonePopover().getJFXPanel();
				Parent root = fxPanel.getScene().getRoot();

				Point pos = Util.getPopOverPosition();
				int x = pos.x;
				int y = pos.y;

				setArrowLocation(Util.getArrowLocation());
				setAnchorLocation(Util.getAnchorLocation());

				if (!isShowing()) {
					show(root, x, y);
					applyStyle();
					show(root, x, y);
					setContentNode(createPopoverContent(msg, icon, textColor, textSize));
				}
			}
		});

		try {
			Thread.sleep(DropzonePopOver.POPOVER_DURATION);
		} catch (InterruptedException e) {

		}

		hidePopOver();
	}

	/**
	 * Applies custom popover style
	 */
	private void applyStyle() {
		// apply style
		((Parent) getSkin().getNode()).getStylesheets().clear();
		((Parent) getSkin().getNode()).getStylesheets()
				.addAll(DropzonePopOver.class.getResource("/style/MyPopOverNormal.css").toExternalForm());
		// fxPanel.getScene().getRoot().requestLayout();
		MyPopOverSkin skin = new MyPopOverSkin(getPopOverUI(), DropzonePopOver.WIDTH, DropzonePopOver.HEIGHT);
		setSkin(skin);

	}

	/**
	 * Creates popover label
	 * 
	 * @param hovered
	 * @return
	 */
	private Label createPopoverContent(boolean hovered) {
		Label label = null;
		if (hovered) {
			String msg = I18n.get("dropzone.releasemouse");
			ImageView icon = new ImageView(
					DropzoneDragEvent.class.getResource("/images/drop_icon_dark.png").toString());
			label = createPopoverContent(msg, icon, DropzonePopOver.TEXT_COLOR_HOVERED,
					DropzonePopOver.POPOVER_TEXT_SIZE_LARGE);
		} else {
			String msg = I18n.get("dropzone.drophere");
			ImageView icon = new ImageView(DropzoneDragEvent.class.getResource("/images/drop_icon.png").toString());
			label = createPopoverContent(msg, icon, DropzonePopOver.TEXT_COLOR_DEFAULT,
					DropzonePopOver.POPOVER_TEXT_SIZE_LARGE);
		}
		return label;
	}

	/**
	 * Creates a <{@link Label} containing text and icon
	 * 
	 * @param hovered
	 *            - Use darker icon if mouse is hovered
	 * @return <{@link Label}
	 */
	private Label createPopoverContent(String msg, ImageView icon, Color textColor, int fontSize) {
		Label label = new Label(msg);
		label.setGraphic(icon);
		label.setTextFill(textColor);
		label.setFont(Font.font("SansSerif", fontSize));
		label.setAlignment(javafx.geometry.Pos.CENTER);
		label.setGraphicTextGap(15);
		label.setContentDisplay(ContentDisplay.TOP);

		return label;
	}

	/**
	 * Change the level for all handlers attached to the default logger.
	 */
	private static void disableJNativeHookLoggers() {
		Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(java.util.logging.Level.OFF);
		}
	}

	/**
	 * Add mouse motion event listeners to dropzone
	 */
	private void addMouseDragEventListeners() {
		getRoot().setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {

				mouseDragOver(event);
			}
		});

		getRoot().setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(final DragEvent event) {
				mouseDragDropped(event);
			}
		});

		getRoot().setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(final DragEvent event) {
				setContentNode(createPopoverContent(false));
			}
		});
	}

	/**
	 * Handle mouse drop events
	 * 
	 * @param e
	 */
	private void mouseDragDropped(final DragEvent e) {
		final Dragboard db = e.getDragboard();
		boolean success = false;

		if (db.hasFiles()) {
			success = true;
			boolean ctrlKeyPressed = popOver.isCtrlKeyPressed();
			boolean altKeyPressed = popOver.isAltKeyPressed();
			
			popOver.notifyDragEventListener(new DropzoneDragEvent(e, ctrlKeyPressed, altKeyPressed));

		}
		e.setDropCompleted(success);
		e.consume();
	}

	/**
	 * On mouse over, this function accepts all file types and changes dropzone
	 * icon
	 * 
	 * @param e
	 */
	private void mouseDragOver(final DragEvent e) {
		final Dragboard db = e.getDragboard();
		if (db.hasFiles()) {
			e.acceptTransferModes(TransferMode.ANY);
			setContentNode(createPopoverContent(true));
		}
	}

	/**
	 * A reference to the {@link DropzonePopOver} controller
	 * 
	 * @return
	 */
	private DropzonePopOver getDropzonePopover() {
		return popOver;
	}
}
