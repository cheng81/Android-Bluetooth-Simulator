package dk.itu.android.bluetooth;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Parcel;
import android.util.Log;
import dk.itu.android.btemu.service.BTService;

public class BluetoothDevice implements Serializable {
	
	//constants
	public static final String ACTION_ACL_CONNECTED = "dk.android.bluetooth.device.action.ACL_CONNECTED";
	public static final String ACTION_ACL_DISCONNECGTED = "dk.android.bluetooth.device.action.ACL_DISCONNECTED";
	public static final String ACTION_ACL_DISCONNECT_REQUEST = "dk.android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED";
	public static final String ACTION_BOND_STATE_CHANGED = "dk.android.bluetooth.device.action.BOND_STATE_CHANGED";
	public static final String ACTION_CLASS_CHANGED = "dk.android.bluetooth.device.action.CLASS_CHANGED";
	public static final String ACTION_FOUND = "dk.android.bluetooth.device.action.FOUND";
	public static final String ACTION_NAME_CHANGED = "dk.android.bluetooth.device.action.NAME_CHANGED";
	public static final String EXTRA_BOND_STATE = "dk.android.bluetooth.device.extra.BOND_STATE";
	public static final String EXTRA_CLASS = "dk.android.bluetooth.device.extra.CLASS";
	public static final String EXTRA_DEVICE = "dk.android.bluetooth.device.extra.DEVICE";
	public static final String EXTRA_NAME = "dk.android.bluetooth.device.extra.NAME";
	public static final String EXTRA_PREVIOUS_BOND_STATE = "dk.android.bluetooth.device.extra.PREVIOUS_BOND_STATE";
	public static final String EXTRA_RSSI = "dk.android.bluetooth.device.extra.RSSI";
	
	public static final int BOND_BONDED = 12;
	public static final int BOND_BONDING = 11;
	public static final int BOND_NONE = 10;
	public static final int ERROR = 0x80000000;
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws IOException {
		String uuids = uuid.toString();
		for(BTService s : services) {
			if(s.getUuid().equals(uuids)) {
				Log.i("BT_DEVICE", "found service "+s.getUuid()+" on device "+addr);
				return new BluetoothSocket(this.tcpAddr,s.getTcpPort(),this);
			}
				
		}
		return null;
	}
	public int describeContents(){
		return -1;
	}
	public boolean equals(Object o) {
		return false;
	}
	public String getAddress(){ return addr; }
	public BluetoothClass getBluetoothClass(){ return btClass; }
	int getBondState() {
		return BOND_NONE;
	}
	public String getName() {
		return name;
	}
	public int hashCode() {
		return -1;
	}
	public String toString() {
		return "";
	}
	public void writeToParcel(Parcel out, int flags) {
		//not supported?
	}
	//
	
	BluetoothClass btClass;
	String addr;
	String tcpAddr;
	String name;
	List<BTService> services;
	
	/**
	 * THIS IS NOT PART OF THE ANDROID PLATFORM, IGNORE THIS METHOD!
	 * @param uuid
	 * @param port
	 */
	public BluetoothDevice(String btAddr, String tcpAddr) {
		this.addr = btAddr;
		this.tcpAddr = tcpAddr;
		this.services = new ArrayList<BTService>();
		this.btClass = new BluetoothClass(
			android.bluetooth.BluetoothClass.Device.PHONE_SMART,
			android.bluetooth.BluetoothClass.Device.Major.PHONE,
			android.bluetooth.BluetoothClass.Service.NETWORKING);
	}
	
	/**
	 * THIS IS NOT PART OF THE ANDROID PLATFORM, IGNORE THIS METHOD!
	 * @param uuid
	 * @param port
	 */
	public void addService(String uuid, int port) {
		this.services.add(new BTService(uuid,port));
	}
	
	/**
	 * THIS IS NOT PART OF THE ANDROID PLATFORM, IGNORE THIS METHOD!
	 * @param uuid
	 * @param port
	 */
	public String getAddr() {
		return addr;
	}
	/**
	 * THIS IS NOT PART OF THE ANDROID PLATFORM, IGNORE THIS METHOD!
	 * @param uuid
	 * @param port
	 */
	public String getTcpAddr() {
		return tcpAddr;
	}
}
