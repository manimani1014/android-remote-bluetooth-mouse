package com.david_tvildiani.rbmouse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class MainActivity extends Activity
{

	Context myContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout_Connect);
		layout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this, ScanForBluetoothDevices.class));
			}
		});
	}

}
