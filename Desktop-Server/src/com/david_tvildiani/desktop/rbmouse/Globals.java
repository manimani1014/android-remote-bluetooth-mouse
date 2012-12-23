package com.david_tvildiani.desktop.rbmouse;
import java.util.Vector;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class Globals
{
	private static final Globals glb = new Globals();
	BluetoothServer bluetoothServer = null;
	RTouchpadMainGui MainUI = null;
	Vector<Client> connectedClients = new Vector<Client>();
	static int sampleClientIdGen = Integer.MIN_VALUE;

	public RTouchpadMainGui getMainUI()
	{
		return MainUI;
	}

	public void setMainUI(RTouchpadMainGui mainUI)
	{
		MainUI = mainUI;
	}

	private int genNextID()
	{
		return sampleClientIdGen++;
	}

	public int clientConnected(String info, BluetoothClient bClient)
	{
		int id = genNextID();
		myCustomListItem itm = MainUI.addConnectedClient(info + " : " + id, bClient, id);
		Client c = new Client(id, itm, bClient);
		connectedClients.add(c);
		return id;
	}

	public void clientDisconnected(int id)
	{
		int index = SearchForClients(id);
		if (index != -1)
		{
			Client c = connectedClients.get(index);
			MainUI.removeConnectedClientFromList(c.getListItm());
			connectedClients.remove(index);
		}
	}

	private int SearchForClients(int id)
	{
		for (int i = 0; i < connectedClients.size(); i++)
		{
			Client c = connectedClients.get(i);
			if (c.getId() == id)
			{
				return i;
			}
		}
		return -1;
	}

	public void disconnectClient(int id)
	{
		int index = SearchForClients(id);
		if (index != -1)
		{
			Client c = connectedClients.get(index);
			MainUI.removeConnectedClientFromList(c.getListItm());
			c.getBluetoothClient().disconnect();
			connectedClients.remove(index);
		}
	}

	public BluetoothServer getBluetoothServer()
	{
		return bluetoothServer;
	}

	public void setBluetoothServer(BluetoothServer bluetoothServer)
	{
		this.bluetoothServer = bluetoothServer;
	}

	private Globals()
	{
	}

	public static Globals getGlobals()
	{
		return glb;
	}

	class Client
	{
		public int getId()
		{
			return id;
		}

		public myCustomListItem getListItm()
		{
			return itm;
		}

		public BluetoothClient getBluetoothClient()
		{
			return client;
		}

		public Client(int id, myCustomListItem itm, BluetoothClient client)
		{
			super();
			this.id = id;
			this.itm = itm;
			this.client = client;
		}

		int id;
		myCustomListItem itm;
		BluetoothClient client;
	}

}
