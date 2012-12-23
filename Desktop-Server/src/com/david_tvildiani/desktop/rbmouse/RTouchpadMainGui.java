package com.david_tvildiani.desktop.rbmouse;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class RTouchpadMainGui
{
	public final static UUID mmServiceUUID = new UUID("8d53e16622d548bd945135a8d2eb902d", false);
	public final static String mmServiceName = "RemoteMouseService";
	public final static String mmServiceURL = "btspp://localhost:" + mmServiceUUID + ";name=" + mmServiceName;
	private static int logCounter = 0;

	private JFrame frmRemoteTouchpadClient;
	private JComboBox<CustomCombBoxItem> comboBox_BluetoothDevices;
	private JTextArea textArea_Log;
	private JButton btn_StartListening;
	private DefaultListModel<myCustomListItem> connectedClientsListModel = new DefaultListModel<myCustomListItem>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					RTouchpadMainGui window = new RTouchpadMainGui();
					window.frmRemoteTouchpadClient.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RTouchpadMainGui()
	{
		initialize();

		myStaffInit();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmRemoteTouchpadClient = new JFrame();
		frmRemoteTouchpadClient.setTitle("Remote TouchPad Client  // Created  by David Tvildiani | DavidTamuna@Gmail.com");
		frmRemoteTouchpadClient.setBounds(100, 100, 450, 300);
		frmRemoteTouchpadClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRemoteTouchpadClient.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmRemoteTouchpadClient.getContentPane().add(tabbedPane);

		JPanel panel_Main = new JPanel();
		tabbedPane.addTab("Main", null, panel_Main, null);
		GridBagLayout gbl_panel_Main = new GridBagLayout();
		gbl_panel_Main.columnWidths = new int[]
		{ 0, 0 };
		gbl_panel_Main.rowHeights = new int[]
		{ 0, 0, 0, 0, 0 };
		gbl_panel_Main.columnWeights = new double[]
		{ 1.0, Double.MIN_VALUE };
		gbl_panel_Main.rowWeights = new double[]
		{ 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_Main.setLayout(gbl_panel_Main);

		JLabel lblNewLabel = new JLabel("Bluetooth devices:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_Main.add(lblNewLabel, gbc_lblNewLabel);

		comboBox_BluetoothDevices = new JComboBox<CustomCombBoxItem>();
		GridBagConstraints gbc_comboBox_BluetoothDevices = new GridBagConstraints();
		gbc_comboBox_BluetoothDevices.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_BluetoothDevices.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_BluetoothDevices.gridx = 0;
		gbc_comboBox_BluetoothDevices.gridy = 1;
		panel_Main.add(comboBox_BluetoothDevices, gbc_comboBox_BluetoothDevices);

		btn_StartListening = new JButton("Start Listening...");
		GridBagConstraints gbc_btn_StartListening = new GridBagConstraints();
		gbc_btn_StartListening.insets = new Insets(0, 0, 5, 0);
		gbc_btn_StartListening.gridx = 1;
		gbc_btn_StartListening.gridy = 1;
		panel_Main.add(btn_StartListening, gbc_btn_StartListening);

		JLabel lblNewLabel_1 = new JLabel("Connected Clients:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panel_Main.add(lblNewLabel_1, gbc_lblNewLabel_1);

		list_ConnectedClients = new JList<myCustomListItem>(connectedClientsListModel);
		list_ConnectedClients.setBackground(new Color(135, 206, 235));
		list_ConnectedClients.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (list_ConnectedClients.getSelectedIndex() != -1)
				{
					myCustomListItem itm = (myCustomListItem) list_ConnectedClients.getSelectedValue();
					int reply = JOptionPane.showConfirmDialog(null, "Whould you like to disconnect", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (reply == JOptionPane.YES_OPTION)
					{
						Globals.getGlobals().disconnectClient(itm.getId());
					}
				}
			}
		});
		list_ConnectedClients.setCellRenderer(new myCustomListCellRenderer());
		GridBagConstraints gbc_list_ConnectedClients = new GridBagConstraints();
		gbc_list_ConnectedClients.gridwidth = 2;
		gbc_list_ConnectedClients.insets = new Insets(0, 0, 0, 5);
		gbc_list_ConnectedClients.fill = GridBagConstraints.BOTH;
		gbc_list_ConnectedClients.gridx = 0;
		gbc_list_ConnectedClients.gridy = 3;
		panel_Main.add(list_ConnectedClients, gbc_list_ConnectedClients);

		panel_Log = new JPanel();
		tabbedPane.addTab("Log", null, panel_Log, null);
		panel_Log.setLayout(new CardLayout(0, 0));

		textArea_Log = new JTextArea();
		panel_Log.add(textArea_Log);
	}

	public void setButtonStatus(boolean isListening)
	{
		if (isListening)
		{

			btn_StartListening.setText("Cancal Listening...");
		} else
		{
			btn_StartListening.setText("Start Listening...");
		}
	}

	Object LOCKOBJECT_LogText = new Object();
	private JPanel panel_Log;
	private JTabbedPane tabbedPane;
	private JList<myCustomListItem> list_ConnectedClients;

	public void postLogText(boolean isCritical, String notification)
	{
		synchronized (LOCKOBJECT_LogText)
		{
			if (isCritical)
			{
				panel_Log.setBackground(Color.red);
				tabbedPane.setSelectedIndex(1);
			}
			textArea_Log.append(String.format("%s: %s \n", Integer.toString(++logCounter), notification));
		}
	}

	public myCustomListItem addConnectedClient(String info, final BluetoothClient client, final int id)
	{
		JLabel lbl = new JLabel(info);
		final myCustomListItem itm = new myCustomListItem(lbl, id);
		connectedClientsListModel.addElement(itm);
		return itm;

	}

	public void removeConnectedClientFromList(myCustomListItem itm)
	{
		connectedClientsListModel.removeElement(itm);
	}

	private void myStaffInit()
	{

		// detecting BlueTooth device
		postLogText(false, "Detecting local Bluetooth device ...");
		LocalDevice xxlocalDevice;
		try
		{
			xxlocalDevice = LocalDevice.getLocalDevice();
		} catch (BluetoothStateException e)
		{
			e.printStackTrace();
			postLogText(true, "Can not initialize bluetooth adapter : " + e.getMessage());
			btn_StartListening.setEnabled(false);
			return;
		}

		String xxDeviceDescription = xxlocalDevice.getFriendlyName() + ": " + xxlocalDevice.getBluetoothAddress();
		postLogText(false, "Detected device: " + xxDeviceDescription);

		// add blueTooth adapter in combo box
		CustomCombBoxItem xxComboBoxItem = new CustomCombBoxItem();
		xxComboBoxItem.setDevice(xxlocalDevice);
		xxComboBoxItem.setName(xxDeviceDescription);
		comboBox_BluetoothDevices.addItem(xxComboBoxItem);

		// initialize globals and server for listening incoming connections
		Globals g = Globals.getGlobals();
		g.setMainUI(this);
		g.setBluetoothServer(new BluetoothServer(mmServiceURL));

		btn_StartListening.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				BluetoothServer server = Globals.getGlobals().getBluetoothServer();
				if (server.IslisteningForClients())
				{
					server.stopListening();
					setButtonStatus(false);
				} else
				{
					server.startListening();
					setButtonStatus(true);
				}
			}
		});
	}

}

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class myCustomListCellRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 8425105811079901718L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{

		if (value instanceof myCustomListItem)
		{
			myCustomListItem itm = (myCustomListItem) value;
			return itm.getLbl();
		}

		return new JLabel("Error");
	}

}