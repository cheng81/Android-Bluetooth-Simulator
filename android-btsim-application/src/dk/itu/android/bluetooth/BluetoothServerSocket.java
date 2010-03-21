package dk.itu.android.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import dk.itu.android.btemu.service.BTEmulator;

public class BluetoothServerSocket {
	static final String TAG = "BTEMU_SERVERSOCKET";

	final ExecutorService executor = Executors.newFixedThreadPool(1);
	BTEmulator emulator;
	Timer timer = null;
	ServerSocket socket;
	
	int port;
	String UUID;
	
	protected BluetoothServerSocket(BTEmulator emulator,String UUID,int port) {
		this.emulator = emulator;
		this.port = port;
		this.UUID = UUID;
		try {
			this.socket = new ServerSocket(port);
			emulator.addService(UUID, port);
		} catch (IOException e) {
			Log.e(TAG, "cannot create server socket", e);
		}
	}
	
	private BluetoothSocket createBTSocket(Socket s) throws IOException {
		InputStream is = s.getInputStream();
		Log.i(TAG, "creating btsocket, reading btaddr...");
		int read;
		byte[] buf = new byte[25];

		
		
		int idx = 0;
		do {
			read = is.read();
			buf[idx] = (byte)read;
			idx++;
		} while( '\n' != read );
		
		String btaddr = new String(buf,0,idx-1);
		
		Log.i(TAG, "received btaddr: " + btaddr);
		BluetoothDevice d = emulator.lookupBT(btaddr.trim());
		return new BluetoothSocket(s, d);
	}
	
	public BluetoothSocket accept() throws IOException {
		//block until a conn is established
		Socket s = socket.accept();
		return createBTSocket(s);
	}
	public BluetoothSocket accept(int timeout) throws IOException {
		//block until a conn is established with timeout
		FutureTask<Socket> future = new FutureTask<Socket>(new Callable<Socket>() {
			@Override
			public Socket call() throws Exception {
				return socket.accept();
			}
		});
		executor.execute(future);
		try {
			Socket s = future.get(timeout, TimeUnit.SECONDS);
			return createBTSocket(s);
		} catch(Exception e) {
			Log.e(TAG, "accept(timeout) timed out? ",e);
			throw new IOException("timed out");
		}
	}
	public void close() throws IOException {
		emulator.removeService(this.UUID, this.port);
		socket.close();
	}
}
