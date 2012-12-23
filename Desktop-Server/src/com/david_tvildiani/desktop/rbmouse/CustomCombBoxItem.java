package com.david_tvildiani.desktop.rbmouse;
import javax.bluetooth.LocalDevice;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class CustomCombBoxItem {
	LocalDevice  mmDevice;
	String mmName;
	
	public LocalDevice getDevice() {
		return mmDevice;
	}
	public void setDevice(LocalDevice mmDevice) {
		this.mmDevice = mmDevice;
	}
	public String getName() {
		return mmName;
	}
	public void setName(String mmName) {
		this.mmName = mmName;
	}
	
	@Override
	public String toString() {
		return  mmDevice.getFriendlyName() + ": " + mmDevice.getBluetoothAddress();
	}
	
}
