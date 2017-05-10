package org.mn.dropzone.popover;

import javax.swing.event.EventListenerList;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.mn.dropzone.eventlistener.DropzoneDragEvent;
import org.mn.dropzone.eventlistener.DropzoneDragEventListener;
import org.mn.dropzone.util.Util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class DropzonePopOver implements NativeMouseInputListener, NativeKeyListener {

	public static final int WIDTH = 330;
	public static final int HEIGHT = 140;
	
	public static final int POPOVER_TEXT_SIZE_DEFAULT = 12;
	public static final int POPOVER_TEXT_SIZE_LARGE = 16;
	public static final double POPOVER_OPACITY = 0.88;

	public static final Color TEXT_COLOR_DEFAULT = Color.rgb(220, 220, 220);
	public static final Color TEXT_COLOR_HOVERED = Color.rgb(120, 120, 120);
	
	public static final int POPOVER_DURATION = 3500;

	private JFXPanel fxPanel;
	private DropzonePopOverUI popOverUI;

	private boolean isCtrlKeyPressed = false;

	/**
	 * 
	 * @param pos
	 */
	public DropzonePopOver() {
		fxPanel = new JFXPanel();
		initUI();
		initNativeMouseMotionListener();

		// show hide once to fix position bug
		popOverUI.showPopOver(false);
		popOverUI.hidePopOver();
	}

	/**
	 * Use jnativehook to detect mouse drag events and show popover
	 */
	private void initNativeMouseMotionListener() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e1) {
			e1.printStackTrace();
		}
		GlobalScreen.addNativeMouseMotionListener(this);
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeKeyListener(this);
	}

	/**
	 * Init JavaFX components
	 */
	private void initUI() {
		// Run on the JavaFX thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
			}
		});
		popOverUI = new DropzonePopOverUI(this);

	}

	/**
	 * Init JFXPanel which serves as a bridge between Swing and JavaFX
	 * 
	 * @param fxPanel
	 */
	private void initFX(JFXPanel fxPanel) {
		// This method is invoked on the JavaFX thread
		Group root = new Group();
		Scene scene = new Scene(root, Color.ALICEBLUE);
		fxPanel.setScene(scene);
	}

	public JFXPanel getJFXPanel() {
		return fxPanel;
	}

	public boolean isCtrlKeyPressed() {
		return isCtrlKeyPressed;
	}

	/**
	 * Show popover with given message and icon
	 */
	public void showMessage(String msg, ImageView icon, Color textColor, int textSize) {
		popOverUI.showPopOver(msg, icon, textColor, textSize);
	}

	/*
	 * Listeners #############################################################
	 */
	private EventListenerList listeners = new EventListenerList();

	public void addDragEventListener(DropzoneDragEventListener listener) {
		listeners.add(DropzoneDragEventListener.class, listener);
	}

	/**
	 * Notify Listeners of a date data change
	 * 
	 * @param event
	 */
	protected synchronized void notifyDragEventListener(DropzoneDragEvent event) {
		for (DropzoneDragEventListener l : listeners.getListeners(DropzoneDragEventListener.class)) {
			l.handleDragEvent(event);
		}
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeEvent) {	
		// check if ctrl is pressed
		isCtrlKeyPressed = (nativeEvent.getModifiers() & NativeKeyEvent.CTRL_MASK) > 0;
		popOverUI.hidePopOver();
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
		if (Util.isInActivationZone(nativeEvent)) {
			// trigger show popover
			popOverUI.showPopOver(false);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
			isCtrlKeyPressed = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
			isCtrlKeyPressed = false;
		}
	}

}