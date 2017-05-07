package org.mn.dropzone;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.View;

import org.mn.dropzone.i18n.I18n;
import org.mn.dropzone.model.ScreenModel;
import org.mn.dropzone.model.ScreenPosition;
import org.mn.dropzone.util.ConfigIO;
import org.mn.dropzone.util.SwingUtil;
import org.mn.dropzone.util.Util;


/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class SettingsDialog extends JPanel {

	private static final long serialVersionUID = 5427919360246052930L;
	private static final JLabel RESIZER = new JLabel();
	private JTextField userInput, serverInput, pathInput;
	private JComboBox<ScreenModel> screenBox;
	private JComboBox<ScreenPosition> hotCornerBox;
	private JCheckBox useMasterPasswordInput;
	private JPasswordField masterPwdInput, pwdInput;
	private JLabel masterPwdLabel;

	public SettingsDialog() {
		initUI();
		setConfigValues();
	}

	private void initUI() {
		UIManager.put("OptionPane.border", new EmptyBorder(0, 0, 10, 0));

		this.setLayout(new BorderLayout(0, 10));
		this.add(createHeader(), BorderLayout.NORTH);
		this.add(createCredentialsInput(), BorderLayout.SOUTH);
		this.add(createHotCorners(), BorderLayout.CENTER);
	}

	private void setConfigValues() {
		ConfigIO cfg = ConfigIO.getInstance();
		if (cfg.getUsername() != null) {
			userInput.setText(cfg.getUsername());
		}

		if (cfg.getPassword() != null) {
			pwdInput.setText(cfg.getPassword());
		}

		if (cfg.getPassword() != null) {
			pwdInput.setText(cfg.getPassword());
		}

		if (cfg.isMasterPwdEnabled()) {
			useMasterPasswordInput.setSelected(true);
			setMasterPwdInputEnabled(true);
		}

		if (cfg.getMasterPassword() != null) {
			masterPwdInput.setText(cfg.getMasterPassword());
		}

		if (cfg.getServerUrl() != null) {
			serverInput.setText(cfg.getServerUrl());
		}

		if (cfg.getStoragePath() != null) {
			pathInput.setText(cfg.getStoragePath());
		}
		if (cfg.getScreenId() != null) {
			ScreenModel screen = Util.getScreen(cfg.getScreenId());
			DefaultComboBoxModel<ScreenModel> screenBoxModel = new DefaultComboBoxModel<ScreenModel>(
					(ScreenModel[]) Util.getScreens());
			screenBoxModel.setSelectedItem(screen);
			screenBox.setModel(screenBoxModel);
		}
		if (cfg.getScreenId() != null) {
			ScreenPosition pos = Util.getScreenPosition(cfg.getScreenPositionId());
			DefaultComboBoxModel<ScreenPosition> cornerBoxModel = new DefaultComboBoxModel<ScreenPosition>(
					(ScreenPosition[]) Util.getScreenPositions());
			cornerBoxModel.setSelectedItem(pos);
			hotCornerBox.setModel(cornerBoxModel);
		}
	}

	private JPanel createHotCorners() {
		// main body
		JPanel main = new JPanel();
		main.setBorder(new EmptyBorder(10, 0, 0, 0));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		// Create and populate the panel.
		JPanel hotCorners = new JPanel(new SpringLayout());

		MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY);
		TitledBorder tb = new TitledBorder(mb, I18n.get("settings.hotcornertitle"), TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION);
		hotCorners.setBorder(tb);

		JLabel cornerLabel = new JLabel(I18n.get("settings.hotcorner"), JLabel.TRAILING);
		hotCorners.add(cornerLabel);
		hotCornerBox = new JComboBox<>();
		cornerLabel.setLabelFor(hotCornerBox);
		hotCorners.add(hotCornerBox);
		DefaultComboBoxModel<ScreenPosition> cornerBoxModel = new DefaultComboBoxModel<ScreenPosition>(
				(ScreenPosition[]) Util.getScreenPositions());
		hotCornerBox.setModel(cornerBoxModel);

		JLabel userLabel = new JLabel(I18n.get("settings.hotcornerscreen"), JLabel.TRAILING);
		hotCorners.add(userLabel);
		screenBox = new JComboBox<>();

		DefaultComboBoxModel<ScreenModel> screenBoxModel = new DefaultComboBoxModel<ScreenModel>(
				(ScreenModel[]) Util.getScreens());
		screenBox.setModel(screenBoxModel);

		hotCorners.add(screenBox);

		// Lay out the panel.
		SwingUtil.makeCompactGrid(hotCorners, 2, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		main.add(hotCorners);
		return main;
	}

	private JPanel createCredentialsInput() {
		// main body
		JPanel main = new JPanel();
		main.setBorder(new EmptyBorder(10, 0, 10, 0));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		// Create and populate the panel.
		JPanel credentialsPanel = new JPanel(new SpringLayout());

		MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY);
		TitledBorder tb = new TitledBorder(mb, I18n.get("settings.credentials"), TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION);
		credentialsPanel.setBorder(tb);

		JLabel serverLabel = new JLabel(I18n.get("settings.server"), JLabel.TRAILING);
		credentialsPanel.add(serverLabel);
		serverInput = new JTextField();
		serverLabel.setLabelFor(serverInput);
		credentialsPanel.add(serverInput);

		JLabel pathLabel = new JLabel(I18n.get("settings.remotepath"), JLabel.TRAILING);
		credentialsPanel.add(pathLabel);
		pathInput = new JTextField();
		pathLabel.setLabelFor(pathInput);
		credentialsPanel.add(pathInput);

		JLabel userLabel = new JLabel(I18n.get("settings.username"), JLabel.TRAILING);
		credentialsPanel.add(userLabel);
		userInput = new JTextField();
		userLabel.setLabelFor(userInput);
		credentialsPanel.add(userInput);

		JLabel pwdLabel = new JLabel(I18n.get("settings.password"), JLabel.TRAILING);
		credentialsPanel.add(pwdLabel);
		pwdInput = new JPasswordField();
		pwdLabel.setLabelFor(pwdInput);
		credentialsPanel.add(pwdInput);

		JLabel enableMasterPwdLabel = new JLabel(I18n.get("settings.usemasterpwd"), JLabel.TRAILING);
		credentialsPanel.add(enableMasterPwdLabel);
		useMasterPasswordInput = new JCheckBox();
		useMasterPasswordInput.setSelected(false);
		useMasterPasswordInput.setToolTipText(I18n.get("settings.masterpwddesc"));
		enableMasterPwdLabel.setLabelFor(useMasterPasswordInput);
		credentialsPanel.add(useMasterPasswordInput);
		useMasterPasswordInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMasterPwdInputEnabled(useMasterPasswordInput.isSelected());
			}
		});

		masterPwdLabel = new JLabel(I18n.get("settings.masterpwd"), JLabel.TRAILING);
		masterPwdLabel.setEnabled(false);
		credentialsPanel.add(masterPwdLabel);
		masterPwdInput = new JPasswordField();
		masterPwdInput.setToolTipText(I18n.get("settings.masterpwddesc"));
		masterPwdInput.setEnabled(false);
		masterPwdLabel.setLabelFor(masterPwdInput);
		credentialsPanel.add(masterPwdInput);

		// Lay out the panel.
		SwingUtil.makeCompactGrid(credentialsPanel, 6, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		main.add(credentialsPanel);
		return main;
	}

	/**
	 * Creates the header of the settings dialog
	 * 
	 * @return
	 */
	private JPanel createHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBorder(new EmptyBorder(0, 0, 0, 0));

		JLabel headerIcon = new JLabel();
		headerIcon.setIcon(new ImageIcon(new ImageIcon(Dropzone.class.getResource("/images/settings.png")).getImage()
				.getScaledInstance(400, 210, Image.SCALE_SMOOTH)));
		header.add(headerIcon, BorderLayout.NORTH);

		JLabel area = new JLabel();
		area.setForeground(Color.DARK_GRAY);
		final String s = "<html>" + (I18n.get("settings.infotext")) + "</html>";
		area.setText(s);

		JPanel infoWrapperPanel = new JPanel();
		infoWrapperPanel.setLayout(new BoxLayout(infoWrapperPanel, BoxLayout.Y_AXIS));

		JPanel topSpacerPanel = new JPanel();
		topSpacerPanel.setBackground(new Color(255, 243, 179));

		JPanel bottomSpacerPanel = new JPanel();
		bottomSpacerPanel.setBackground(new Color(255, 243, 179));

		JPanel infoTextPanel = new JPanel(new BorderLayout());
		infoTextPanel.setBorder(new EmptyBorder(0, 10, 10, 0));
		infoTextPanel.setPreferredSize(getPreferredSize(s, true, 400));
		infoTextPanel.setBackground(new Color(255, 243, 179));
		infoTextPanel.add(area, BorderLayout.NORTH);

		infoWrapperPanel.add(topSpacerPanel);
		infoWrapperPanel.add(infoTextPanel);
		infoWrapperPanel.add(bottomSpacerPanel);

		header.add(infoWrapperPanel, BorderLayout.SOUTH);
		return header;
	}

	public JTextField getUserName() {
		return userInput;
	}

	public JPasswordField getPassword() {
		return pwdInput;
	}

	public JPasswordField getMasterPwd() {
		return masterPwdInput;
	}

	public boolean isMasterPwdEnabled() {
		return useMasterPasswordInput.isSelected();
	}

	public JTextField getServerUrl() {
		return serverInput;
	}

	public JTextField getStoragePath() {
		return pathInput;
	}

	public ScreenModel getSelectedScreen() {
		return (ScreenModel) screenBox.getSelectedItem();
	}

	public ScreenPosition getSelectedScreenPosition() {
		return (ScreenPosition) hotCornerBox.getSelectedItem();
	}

	private void setMasterPwdInputEnabled(boolean enable) {
		masterPwdInput.setEnabled(enable);
		masterPwdLabel.setEnabled(enable);
	}

	/**
	 * Returns the preferred size to set a component at in order to render an
	 * html string. You can specify the size of one dimension.
	 */
	private static Dimension getPreferredSize(String html, boolean width, int prefSize) {

		RESIZER.setText(html);

		View view = (View) RESIZER.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);

		view.setSize(width ? prefSize : 0, width ? 0 : prefSize);

		float w = view.getPreferredSpan(View.X_AXIS);
		float h = view.getPreferredSpan(View.Y_AXIS);

		return new java.awt.Dimension((int) Math.ceil(w), (int) Math.ceil(h));
	}
}
