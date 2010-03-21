package dk.itu.android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

public class BluetoothClass implements Parcelable {
	
	public static Parcelable.Creator<BluetoothClass> CREATOR = new Parcelable.Creator<BluetoothClass>() {
		@Override
		public BluetoothClass createFromParcel(Parcel source) {
			BluetoothClass out = new BluetoothClass();
			out.dClass = source.readInt();
			out.mdClass = source.readInt();
			out.services = source.createIntArray();
			return out;
		}
		@Override
		public BluetoothClass[] newArray(int size) {
			return new BluetoothClass[size];
		}
	};
	
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
		int out = dClass+mdClass;
		for(int i : services) { out+=i; }
		return out;
	}
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(dClass);
		out.writeInt(mdClass);
		out.writeIntArray(services);
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
