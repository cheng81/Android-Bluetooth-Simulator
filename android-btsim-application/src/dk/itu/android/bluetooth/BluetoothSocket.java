package dk.itu.android.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BluetoothSocket {

	//this is provided if the socket was created using the bt socket server
	Socket socket = null;
	
	//these are provided if device.open bla bla was called
	String ip;
	int port;
	
	BluetoothDevice remote;
	
	protected BluetoothSocket( String ip,int port,BluetoothDevice device ) {
		this.ip = ip;
		this.port = port;
		this.remote = device;
	}
	protected BluetoothSocket(Socket s,BluetoothDevice device){
		this.socket = s;
		this.remote = device;
	}
	
	public void close() throws IOException {
		socket.close();
	}
	public void connect() throws IOException {
		if(socket == null) {
			socket = new Socket(ip,port);
			OutputStream os = getOutputStream();
			os.write( (BluetoothAdapter.getDefaultAdapter().getAddress()+"\n").getBytes() );
			os.flush();
		}
	}
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}
	public BluetoothDevice getRemoteDevice() {
		return remote;
	}
}
