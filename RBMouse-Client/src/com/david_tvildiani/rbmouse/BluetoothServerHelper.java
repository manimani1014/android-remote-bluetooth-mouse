package com.david_tvildiani.rbmouse;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 *
 */
public class BluetoothServerHelper extends Thread
{
	
	public BluetoothServerHelper(BluetoothDevice mmDevice, Handler mmHandler)
	{
		this.mmDevice = mmDevice;
		this.mmHandler = mmHandler;
	}

	// IMPORTANT THIS IS SERVICE URL TO CONNECT
	UUID mmUUID = UUID.fromString("8d53e166-22d5-48bd-9451-35a8d2eb902d");

	BluetoothDevice mmDevice;
	Handler mmHandler;

	@Override
	public void run()
	{

		// IMPORTANT TO CANCALL DISCOVERY BEFORE TRY TO CONNECT
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

		BluetoothSocket mmSocket = null;
		Message m = new Message();

		try
		{
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(mmUUID);
			mmSocket.connect();
		} catch (IOException e)
		{

			m.what = -1; // CONNECTION FAILED
			m.obj = e.getMessage(); // SEND FAIURE MESSAGE
			if (mmHandler != null)
				mmHandler.sendMessage(m);
			Log.d("App_Debug", "Failed To connect to UUID");
			return;

		}

		m.what = 1; // CONNECTION SUCESSED
		m.obj = mmSocket; // SEND CONNECTED SOCKET
		if (mmHandler != null)
			mmHandler.sendMessage(m);
	}
}
