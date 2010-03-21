package dk.itu.btemu.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BtEmuClient {
	static final String UTF8 = "UTF-8";
	
	static final int JOIN = 0;
	static final int LEAVE = 1;
	static final int DISCOVERY = 2;
	static final int MODIFYSERVICE = 3;
	
	class Device {
		String btAddr;
		String ipAddr;
		List<Service> services = new ArrayList<Service>();
		
		public String getBtAddr() {
			return btAddr;
		}
		public String getIpAddr() {
			return ipAddr;
		}
		public List<Service> getServices() {
			return services;
		}
	}
	class Service {
		String uuid;
		int port;
		
		public int getPort() {
			return port;
		}
		public String getUuid() {
			return uuid;
		}
	}

	String fakeBluetoothAddress;
	
	String serverAddr;
	int serverPort;
	
	Socket socket;
	BufferedReader isReader;
	OutputStream os;
	Thread clientThread;
	
	boolean running = true;
	
	public BtEmuClient(String btAddr, String addr,int port) {
		this.fakeBluetoothAddress = btAddr;
		this.serverAddr = addr;
		this.serverPort = port;
	}
	
	public BtEmuClient(String btAddr) {
		this(btAddr,"localhost",8199);
	}
	
	public void start() {
		try {
			socket = new Socket(this.serverAddr,this.serverPort);
			os = socket.getOutputStream();
			isReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("cannot connect to server!");
		}
	}
	public void stop() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void join() {
		send( JOIN,stdCmd() );
	}
	public void leave() {
		send( LEAVE,stdCmd() );
	}
	public void addService(String uuid,int port) {
		modifyService(uuid,port,true);
	}
	public void removeService(String uuid) {
		modifyService(uuid,0,false);
	}
	void log(String tag,String s){
		System.out.println(tag+"\n\t"+s);
	}
	private void modifyService(String uuid, int port, boolean added) {
		Map<String,String> m = stdCmd();
		m.put("type", added ? "added" : "removed");
		m.put("tcp.port", port+"");
		m.put("service.uuid",uuid);
		send( MODIFYSERVICE,m );
	}
	public Set<Device> discovery() {
		Set<Device> out = new HashSet<Device>();
		send( DISCOVERY,stdCmd() );
		
		String line = null;
		do {
			try {
				line = isReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("cannot read inputstream!");
			}
			log("DISCOVERY_CMD", "device: " + line);
			if(line==null) break;
			
			String[] parts = line.trim().split("--");
			Device d = new Device();
			d.btAddr = parts[0];
			d.ipAddr = parts[1];
			
			if(parts.length>2) {
				if(parts[2].length()>0) {
					String[] sParts = parts[2].split("<><>");
					for(String p : sParts) {
						String[] s = p.split("<>");
						Service srv = new Service();
						srv.uuid = s[0];
						srv.port = Integer.parseInt(s[1]);
						d.services.add(srv);
					}
				}
			}
			out.add(d);
		} while(line != null);
		
		return out;
	}
	
	public Map<String,String> stdCmd() {
		Map<String,String> out = new HashMap<String,String>();
		out.put("bt.address", this.fakeBluetoothAddress);
		out.put("tcp.address", "127.0.0.1");
		out.put("not.android.emulator", "true");
		return out;
	}
	
	void send(int cmdType, Map<String,String> command) {
		try {
			os.write( (cmdType+"]").getBytes(UTF8) );
			for(String k : command.keySet()) {
				os.write( (k+"="+command.get(k)+"&").getBytes(UTF8) );
			}
			os.write("]".getBytes(UTF8));
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
