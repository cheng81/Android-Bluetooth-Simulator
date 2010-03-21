package dk.itu.android.btemu.service;

import android.os.Parcel;
import android.os.Parcelable;

public class BTService implements Parcelable {

	public static Parcelable.Creator<BTService> CREATOR = new Parcelable.Creator<BTService>() {
		@Override
		public BTService createFromParcel(Parcel source) {
			BTService out = new BTService();
			out.tcpPort = source.readInt();
			out.uuid = source.readString();
			return out;
		}
		@Override
		public BTService[] newArray(int size) {
			return new BTService[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(tcpPort);
		dest.writeString(uuid);
	}
	
	int tcpPort;
	String uuid;
	
	public BTService(){}
	public BTService(String uuid, int port) {
		this.uuid = uuid;
		this.tcpPort = port;
	}
	
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getTcpPort() {
		return tcpPort;
	}
	public String getUuid() {
		return uuid;
	}

}
