package dk.itu.android.bluetooth;

import java.io.Serializable;

import android.os.Parcel;

public class BluetoothClass implements Serializable {
	
	public static class Device {
		public static class Major {
			public static final int PHONE = android.bluetooth.BluetoothClass.Device.Major.PHONE;
			public static final int COMPUTER = android.bluetooth.BluetoothClass.Device.Major.COMPUTER;
		}
		public static final int PHONE_SMART = android.bluetooth.BluetoothClass.Device.PHONE_SMART;
	}
	public static class Service {
		public static final int NETWORKING = android.bluetooth.BluetoothClass.Service.NETWORKING;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int dClass;
	int mdClass;
	int[] services;
	
	//needed by the serialization framework stuff
	/**
	 * THIS IS NOT PART OF THE ANDROID BLUETOOTH FRAMEWORK, IGNORE THIS METHOD
	 */
	public BluetoothClass(){}
	protected BluetoothClass(int dClass, int mdClass, int...services) {
		this.dClass = dClass;
		this.mdClass = mdClass;
		this.services = services;
	}
	
	//
	public int describeContents() {
		return -1;
	}
	public boolean equals(Object o) {
		return false;
	}
	public int getDeviceClass() {
		return dClass;
	}
	public int getMajorDeviceClass() {
		return mdClass;
	}
	public boolean hasService(int service) {
		for(int i : services) {
			if (i == service) return true;
		}
		return false;
	}
	public int hashCode() {
		return -1;
	}
	public void writeToParcel(Parcel out, int flags) {
		//not supported?
	}
	//
	
	//serialization stuff
	//
	public int getdClass() {
		return dClass;
	}
	public void setdClass(int dClass) {
		this.dClass = dClass;
	}
	public int getMdClass() {
		return mdClass;
	}
	public void setMdClass(int mdClass) {
		this.mdClass = mdClass;
	}
	public int[] getServices() {
		return services;
	}
	public void setServices(int[] services) {
		this.services = services;
	}
	//
	
}
