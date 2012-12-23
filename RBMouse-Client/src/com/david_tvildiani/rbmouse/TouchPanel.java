package com.david_tvildiani.rbmouse;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 * 
 */
public class TouchPanel extends Activity implements android.view.GestureDetector.OnGestureListener, OnDoubleTapListener
{

	Button Button_Touch_Left;
	Button Button_Touch_Right;
	BluetoothSocket bSocket = null;
	DataOutputStream mmOutputStream;
	GestureDetector mmGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse_touch_panel);
		Button_Touch_Left = (Button) findViewById(R.id.Button_Touch_Left);
		Button_Touch_Right = (Button) findViewById(R.id.Button_Touch_Right);
		View Touch_Panel = (View) findViewById(R.id.View_Touch_Panel);

		int id = getIntent().getExtras().getInt("BLUETOOTHSOCKET");

		// TRY TO CONNECT
		Object obj = Globals.getGlobal().getTransferedObject(id);
		bSocket = (BluetoothSocket) obj;

		if (bSocket == null)
		{
			Toast.makeText(this, "NULLSOCKET", Toast.LENGTH_SHORT).show();
			Log.d("App_Debug", "NullSOCKET");
			finish();
			return;
		}

		try
		{
			mmOutputStream = new DataOutputStream(bSocket.getOutputStream());
		} catch (IOException e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			finish();

			return;
		}

		// PREPARE
		Touch_Panel.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return mmGestureDetector.onTouchEvent(event);
			}
		});
		Button_Touch_Left.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{

				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setPressed(true);
					writeMessage(BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_DOWN);
					return true;

				case MotionEvent.ACTION_UP:
					v.setPressed(false);
					writeMessage(BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_UP);
					return true;
				}
				return false;
			}
		});

		Button_Touch_Right.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{

				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setPressed(true);
					writeMessage(BlueMoProtocol.ACTION_MOUSE_RIGHT_BUTTON_DOWN);
					return true;

				case MotionEvent.ACTION_UP:
					v.setPressed(false);
					writeMessage(BlueMoProtocol.ACTION_MOUSE_RIGHT_BUTTON_UP);
					return true;
				}
				return false;
			}
		});

		mmGestureDetector = new GestureDetector(this, this);
		mmGestureDetector.setIsLongpressEnabled(false);
		mmGestureDetector.setOnDoubleTapListener(this);
	}

	private void closeBluetoothSockets()
	{
		if (bSocket != null)
		{
			try
			{
				bSocket.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		closeBluetoothSockets();
	}

	private void showToast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void writeMessage(int msg, int x, int y)
	{
		try
		{
			mmOutputStream.writeInt(msg);
			mmOutputStream.writeInt(x);
			mmOutputStream.writeInt(y);
			mmOutputStream.flush();
		} catch (IOException e)
		{
			showToast(e.getMessage());
			finish();
			LOGd(e.getMessage());
		}
	}

	private void writeMessage(int msg)
	{
		try
		{
			mmOutputStream.writeInt(msg);
			mmOutputStream.flush();
		} catch (IOException e)
		{
			showToast(e.getMessage());
			finish();
			LOGd(e.getMessage());
		}

	}

	private void LOGd(String message)
	{
		Log.d("App_Debug", message);
	}

	@Override
	public boolean onDown(MotionEvent arg0)
	{
		// LOGd("onDown");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3)
	{
		// LOGd("onFling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
		// LOGd("onLongPress");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		// LOGd("onScroll");
		// LOGd(e1.getX() + " " + e1.getY() + " || " + e2.getX() + " " +
		// e2.getY() + "||" + distanceX + " " + distanceY);
		writeMessage(BlueMoProtocol.ACTION_MOVE, (int) distanceX, (int) distanceY);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		// LOGd("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// LOGd("onSingleTapUp");
		return false;
	}

	// boolean doubleTappMoved = false;

	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
		// doubleTappMoved = false;
		// LOGd("onDoubleTap");
		return true;

	}

	boolean mmDoubleClickConfirmed;
	long mmMovesAfterDoubleClick = 0;
	float myLastTouchX, myLastTouchY;
	final long doubleClickMovesErrorRate = 3;

	@Override
	public boolean onDoubleTapEvent(MotionEvent e)
	{
		final float xDelta, yDelta;
		switch (e.getAction())
		{

		case MotionEvent.ACTION_DOWN:
			mmDoubleClickConfirmed = true;
			mmMovesAfterDoubleClick = 0;
			writeMessage(BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_DOWN);
			myLastTouchX = e.getX();
			myLastTouchY = e.getY();
			return true;

		case MotionEvent.ACTION_MOVE:
			mmDoubleClickConfirmed = false;
			mmMovesAfterDoubleClick++;
			xDelta = myLastTouchX - e.getX();
			yDelta = myLastTouchY - e.getY();
			writeMessage(BlueMoProtocol.ACTION_MOVE, (int) xDelta, (int) yDelta);
			myLastTouchX = e.getX();
			myLastTouchY = e.getY();
			return true;

		case MotionEvent.ACTION_UP:
			if (mmDoubleClickConfirmed || mmMovesAfterDoubleClick > doubleClickMovesErrorRate)
			{
				// DoubleClick
				writeMessage(BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_UP);
				writeMessage(BlueMoProtocol.ACTION_SINLE_TAPP);
			} else
			{
				// Drag
				writeMessage(BlueMoProtocol.ACTION_MOUSE_LEFT_BUTTON_UP);
			}
			return true;

		}
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
		writeMessage(BlueMoProtocol.ACTION_SINLE_TAPP);
		// LOGd("onSingleTapConfirmed");
		return true;
	}

}
