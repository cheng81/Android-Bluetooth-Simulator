package dk.itu.android.btemu.service;

import java.io.Serializable;

public class BTService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
