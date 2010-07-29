package dk.itu.btemu.state;

import java.util.ArrayList;
import java.util.List;

public class Device {

	String ipAddr;
	String btAddr;
	String name;
	
	boolean isAndroidEmulator = true;

	/**
	 * The port at which is running the emulator
	 */
	int emulatorPort = -1;
	List<Service> services;
	
	public Device(String ip,String bt) {
		this.ipAddr = ip;
		this.btAddr = bt;
		this.services = new ArrayList<Service>();
	}
	
	public void addService(String UUID, int port) {
		services.add(new Service(UUID, port, this));
	}
	public void removeService(String UUID) {
		Service s = getService(UUID);
		services.remove(s);
	}
	
	protected Service getService(String uuid) {
		for(Service s : services) {
			if(s.UUID.equals(uuid))
				return s;
		}
		return null;
	}
	
	public String getBtAddr() {
		return btAddr;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public String getName() {
		return name;
	}
	public List<Service> getServices() {
		return services;
	}
	public void setEmulatorPort(int emulatorPort) {
		this.name = "emulator-"+emulatorPort;
		this.emulatorPort = emulatorPort;
	}
	public int getEmulatorPort() {
		return emulatorPort;
	}
	public boolean isAndroidEmulator() {
		return isAndroidEmulator;
	}
	public void setAndroidEmulator(boolean isAndroidEmulator) {
		this.isAndroidEmulator = isAndroidEmulator;
	}
}
