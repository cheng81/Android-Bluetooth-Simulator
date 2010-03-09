package dk.itu.btemu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import dk.itu.btemu.state.Device;

public class BTMacForwardingsChecker {

	private class DeviceAddr {
		String type;
		String btaddr;
		int port;
		public DeviceAddr(String type, int port) {
			this.type = type; this.port = port;
		}
	}
	
	public void run() {
		System.out.println("getting device list...");
		//get avail devices
		List<DeviceAddr> addresses = getAvailableDevices();
		//for each one, check if a device exists?
		System.out.println("search for bt addresses...");
		searchBTAddresses(addresses);
		updateEmulatorPort(addresses);
	}
	
	private void updateEmulatorPort(List<DeviceAddr> addresses) {
		for(DeviceAddr addr : addresses) {
			Device d = State.getInstance().get(addr.btaddr);
			if(d != null) {
				d.setEmulatorPort(addr.port);
			} else {
				System.out.println("cannot find device with bt.addr: " + addr.btaddr +
					", running on port: " + addr.port);
			}
		}
	}
	
	private void searchBTAddresses(List<DeviceAddr> addresses) {
		for(DeviceAddr addr : addresses) {
			Process p = createProcess(Server.ADB_CMD(), "-s", addr.type+"-"+addr.port,
				"shell",
				"cat /data/data/dk.itu.android.btemu/files/BTADDR.TXT");
			InputStream is = p.getInputStream();
			InputStreamReader inr = new InputStreamReader(is);
			char[] buf = new char[1];
			String s = "";
			int read = 0;
			try {
				do {
					read = inr.read(buf);
					if(read>0)
						s+=buf[0];
				} while(read>0);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("got bluetooth address: " + s);
			addr.btaddr = s;
		}
	}
	private List<DeviceAddr> getAvailableDevices() {
		List<DeviceAddr> out = new ArrayList<DeviceAddr>();
		Process p = createProcess(Server.ADB_CMD(),"devices");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		do {
			try {
				line = br.readLine();
				DeviceAddr cur = parse(line);
				if(cur!=null){
					out.add(cur);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} while(line != null);
		return out;
	}
	private DeviceAddr parse(String line) {
		if(line==null) return null;
		line = line.trim();
		System.out.println("trying to parse: " + line);
		if(line.startsWith("emulator")) {
			line = line.substring("emulator-".length());
			line = line.substring(0, 4);
			int port = Integer.parseInt(line);
			System.out.println("added device running on port: " + port);
			return new DeviceAddr("emulator",port);
		}
		return null;
	}
	
	
	
	public static Process createProcess(String...args) {
		ProcessBuilder pb = new ProcessBuilder(args);
		try {
			return pb.start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
