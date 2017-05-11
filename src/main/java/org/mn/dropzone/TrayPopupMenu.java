package org.mn.dropzone;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mn.dropzone.i18n.I18n;
import org.mn.dropzone.model.AuthModel;
import org.mn.dropzone.model.ScreenModel;
import org.mn.dropzone.model.ScreenPosition;
import org.mn.dropzone.util.ConfigIO;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class TrayPopupMenu extends PopupMenu implements ActionListener {

	private static final long serialVersionUID = -840315326433707133L;
	private MenuItem itemExit, itemSettings;

	public TrayPopupMenu() {
		initUI();
	}

	private void initUI() {

		itemSettings = new MenuItem(I18n.get("tray.settings"));
		itemSettings.addActionListener(this);
		this.add(itemSettings);

		itemExit = new MenuItem(I18n.get("tray.exit"));
		itemExit.addActionListener(this);
		this.add(itemExit);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == itemExit) {
			System.exit(0);
		} else if (e.getSource() == itemSettings) {
			SettingsDialog dialog = new SettingsDialog();
			JFrame frame = new JFrame();
			frame.setAlwaysOnTop(true);
			int result = JOptionPane.showConfirmDialog(frame, dialog, I18n.get("tray.settings"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			frame.dispose();
			
			if (result == JOptionPane.OK_OPTION) {

				String username = dialog.getUserName().getText();
				String serverUrl = dialog.getServerUrl().getText();
				String storagePath = dialog.getStoragePath().getText();
				String password = new String(dialog.getPassword().getPassword());
				String masterPassword = new String(dialog.getMasterPwd().getPassword());
				boolean isMasterPwdEnabled = dialog.isMasterPwdEnabled();

				ScreenModel screenModel = dialog.getSelectedScreen();
				ScreenPosition screenPos = dialog.getSelectedScreenPosition();
				AuthModel authMethod = dialog.getSelectedAuthMethod();

				ConfigIO cfg = ConfigIO.getInstance();
				cfg.setUsername(username);
				
				// important: first set masterpwd
				// and masterpwd enabled before
				// setting the pwd
				cfg.setMasterPwdEnabled(isMasterPwdEnabled);
				cfg.setMasterPassword(masterPassword);
				cfg.setPassword(password);
				cfg.setServerUrl(serverUrl);
				cfg.setStoragePath(storagePath);
				cfg.setScreenId(screenModel.getIdString());
				cfg.setAuthMethod(authMethod.getType().getId());
				cfg.setScreenPositionId(String.valueOf(screenPos.getPos().getId()));
				cfg.save();
			}
		}

	}
}
