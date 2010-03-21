package dk.itu.btemu.state;

import dk.itu.btemu.BTMacForwardingsChecker;
import dk.itu.btemu.Server;

public class Service {

	String UUID;
	Device device;
	int tcpPort;
	/**
	 * The port on which a client should connect
	 * in order to connect to the service
	 * due to networking limitation of the emulator..
	 */
	int hostPort;
	
	public Service(String UUID, int tcpPort, Device device) {
		this.UUID = UUID;
		this.tcpPort = tcpPort;
		this.device = device;
		
		if(device.isAndroidEmulator()) {
			this.hostPort = Server.next();
			createNetworkRedirection();
		} else {
			System.out.println("not-android-emulator service");
			this.hostPort = tcpPort;
		}
		
	}
	private void createNetworkRedirection() {
		System.out.println("creating network forwarding from "+tcpPort+" to "+hostPort);
		Process p = BTMacForwardingsChecker.createProcess(Server.ADB_CMD(),
			"-s","emulator-"+device.emulatorPort,
			"forward",
			"tcp:"+hostPort,
			"tcp:"+tcpPort);
		int exitValue;
		try {
			exitValue = p.waitFor();
			System.out.println("forward process exit value: " + exitValue);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getTcpPort() {
		return tcpPort;
	}
	public int getHostPort() {
		return hostPort;
	}
	public String getUUID() {
		return UUID;
	}
	public Device getDevice() {
		return device;
	}
}
