package dk.itu.btemu.cmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements Runnable {
	public class Param {
		String name;
		String value;
		public Param(String n, String v) {
			this.name = n;
			this.value = v;
		}
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
	}

	Socket socket;
	InputStreamReader in;
	OutputStream out;
	
	List<Param> params;
	String remoteIp;
	
	public BaseCommand(Socket socket, InputStreamReader socketInputStream) {
//		try {
			this.socket = socket;
			in = socketInputStream;
//		} catch(IOException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public void run() {
		try {
			out = socket.getOutputStream();
			params = readParams(in);
			remoteIp = socket.getInetAddress().getHostAddress();
			work(params);
			writeResponse(out);
			out.flush();
//			out.close();
//			in.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(socket.isConnected()) {
				try{socket.close();}catch(IOException ignored){}
			}
		}
		
	}
	
	protected abstract void work( List<Param> params ) throws Exception;
	protected abstract void writeResponse( OutputStream out ) throws IOException;

	protected List<Param> readParams(InputStreamReader in) throws IOException {
		List<Param> out = new ArrayList<Param>();
		String[] cur = null;
		do {
			cur = readParam(in);
			if(cur != null) {
				out.add(new Param(cur[0],cur[1]));
			}
		} while(cur != null);
		return out;
	}

	protected String[] readParam(InputStreamReader inr) throws IOException {
		
		char[] buf = new char[1];
		
		StringBuilder sb = new StringBuilder();
		int count = 0;
		String s = "";
		char last = ' ';
		do {
			count = inr.read(buf);
			sb.append(buf);
			s=sb.toString();
			last = buf[0];
		} while( (last != '&' && last != ']') && count!=-1 );
		
		if(last == ']') {
			return null;
		}
		s = s.substring(0,s.length()-1);
		System.out.println("read parameter: " + s);
		return s.split("=");
	}
	
	protected String getParam(String key) {
		for(Param p : params) {
			if(p.name.equals(key))
				return p.value;
		}
		return null;
	}
}
