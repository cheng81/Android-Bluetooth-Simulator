package dk.itu.android.btemu.service.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import dk.itu.android.bluetooth.BluetoothDevice;

public class Discovery extends NoParamsBaseCommand {
	public interface WithDevices {
		public void devices( List<BluetoothDevice> devices );
	}
	
	@SuppressWarnings("unchecked")
	List<dk.itu.android.bluetooth.BluetoothDevice> devices = new ArrayList();
	WithDevices withDevices = null;
	
	public Discovery( Socket socket ) {
		super(CommandType.DISCOVERY, socket);
	}
	public void setWithDevices(WithDevices wd) {
		this.withDevices = wd;
	}
	
	public List<dk.itu.android.bluetooth.BluetoothDevice> getDevices() {
		return devices;
	}
	
//	@Override
//	protected void readResponse(InputStream in) throws IOException {
//		String all = readAll(in);
//		Log.i("DISCOVERY_CMD", "all response: "+all);
//		for(String line : all.split("\r\n")) {
//			String[] parts = line.trim().split("|");
//			dk.itu.android.bluetooth.BluetoothDevice d = new BluetoothDevice(parts[0], parts[1]);
//			
//			if(parts[3].length()>0) {
//				String[] sParts = parts[3].split("<>");
//				for(String p : sParts) {
//					String[] s = p.split("|");
//					d.addService(s[0], Integer.parseInt(s[1]));
//				}
//			}
//			devices.add(d);
//		}
//		if(withDevices != null) {
//			withDevices.devices(devices);
//		}
//
//	}

	@Override
	protected void readResponse(InputStream in) throws IOException {
		//devices are:
		//bt.addr|ip.addr|uuid<>port|uuid<>port...\r\n
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		do {
			line = br.readLine();
			Log.i("DISCOVERY_CMD", "device: " + line);
			if(line==null) break;
			
			String[] parts = line.trim().split("--");
			dk.itu.android.bluetooth.BluetoothDevice d = new BluetoothDevice(parts[0], parts[1],parts[2]);
			
			String log = "";
			int idx = 0;
			for(String part : parts) {
				log += idx + ". " + part + "\n";
			}
			Log.i("DISCOVERY_CMD",log);
			
			if(parts.length>3) {
				if(parts[3].length()>0) {
					String[] sParts = parts[3].split("<><>");
					Log.i("DISCOVERY_CMD","services: " + parts[3]);
					for(String p : sParts) {
						Log.i("DISCOVERY_CMD",p);
						String[] s = p.split("<>");
						Log.i("DISCOVERY_CMD","Split service length: " + s.length);
						d.addService(s[0], Integer.parseInt(s[1]));
					}
				}
			}
			devices.add(d);
		} while(line != null);
		
		if(withDevices != null) {
			withDevices.devices(devices);
		}
	}

	@Override
	protected void sendParameters(OutputStream out) throws IOException {
	}

}
