package com.david_tvildiani.rbmouse;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class ScanForBluetoothDevices extends Activity
{

	ArrayList<BluetoothDevice> mmBDevices;
	ArrayAdapter<String> mmListviewAdapter;
	Button mmBtnScann;
	Activity mmContext = this;

	private Button mmBtnCancal;

	public Context getThisContext()
	{
		return mmContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scan_for_bluetooth_devices);

		setTitle("Please start scann ");
		mmBDevices = new ArrayList<BluetoothDevice>();
		mmListviewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		mmBtnCancal = (Button) findViewById(R.id.Dialog_Button_Cancal);
		mmBtnScann = (Button) findViewById(R.id.Dialog_Button_ScannForDevices);
		ListView lstDiscoveredDevices = (ListView) findViewById(R.id.Dialog_Listview_DiscoveredDevices);

		// Register broadcast receivers
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mmBluetoothBroadcastReciever, filter);

		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mmBluetoothBroadcastReciever, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		this.registerReceiver(mmBluetoothBroadcastReciever, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mmBluetoothBroadcastReciever, filter);

		lstDiscoveredDevices.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
			{

				// ask if connect
				AlertDialog.Builder builder = new AlertDialog.Builder(getThisContext());
				builder.setCancelable(true);
				builder.setTitle("Connect ?");
				// builder.setInverseBackgroundForced(true);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					// Get BlueTooth device to connect
					BluetoothDevice bDevice = mmBDevices.get(position);

					@Override
					public void onClick(final DialogInterface dialog, int which)
					{
						// Display Progress while trying to connect remote
						// device
						final ProgressDialog pDialog = ProgressDialog.show(getThisContext(), "Status", "Connecting ...");
						pDialog.setCancelable(false);
						// handler which gets notified when connection status
						// changes
						Handler handler = new Handler()
						{

							@Override
							public void handleMessage(Message msg)
							{
								switch (msg.what)
								{
								case -1: // fail
									Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
									if (pDialog.isShowing())
										pDialog.dismiss();
									break;

								case 1: // success
									if (pDialog.isShowing())
										pDialog.dismiss();
									int objectID = Globals.getGlobal().putTransferObject(msg.obj);
									finish();

									Intent intent = new Intent(getThisContext(), TouchPanel.class);
									intent.putExtra("BLUETOOTHSOCKET", objectID);
									startActivity(intent);
									break;
								}
							}

						};

						// Try to connect to remote device new
						(new BluetoothServerHelper(bDevice, handler)).start();

					}
				});

				builder.setNegativeButton("No", null);

				// CREATE AND SHOW ALERT DIALOG
				AlertDialog alert = builder.create();
				alert.show();

			}

		});

		lstDiscoveredDevices.setAdapter(mmListviewAdapter);
		mmBtnScann.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();

				if (!bAdapter.isEnabled())
				{
					startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
				} else
				{
					bAdapter.startDiscovery();
				}

			}
		});

		mmBtnCancal.setEnabled(false);
		mmBtnCancal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			}
		});

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.unregisterReceiver(mmBluetoothBroadcastReciever);
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}

	BroadcastReceiver mmBluetoothBroadcastReciever = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{

			String action = intent.getAction();

			if (action == BluetoothAdapter.ACTION_STATE_CHANGED)
			{

				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				switch (state)
				{
				case BluetoothAdapter.STATE_ON:
					showToast("Bluetooth is On");
					BluetoothAdapter.getDefaultAdapter().startDiscovery();
					break;
				}
			} else if (action.equals(BluetoothDevice.ACTION_FOUND))
			{

				BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				Log.d("App_Debug", "Device found: " + bDevice.getAddress());

				mmBDevices.add(bDevice);
				mmListviewAdapter.add(bDevice.getName() + "(" + bDevice.getAddress() + ")");
				mmListviewAdapter.notifyDataSetChanged();

			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
			{
				mmBtnCancal.setEnabled(true);
				mmBtnScann.setEnabled(false);
				mmListviewAdapter.clear();
				mmBDevices.clear();
				mmListviewAdapter.notifyDataSetChanged();
				setTitle("Scanning...");
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
			{
				setTitle("Scann finished");
				mmBtnScann.setEnabled(true);
				mmBtnCancal.setEnabled(false);
			}

		}

		private void showToast(String message)
		{
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			Log.d("App_Debug", message);
		}
	};

}
