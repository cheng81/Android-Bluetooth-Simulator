package dk.itu.android.btemu.service.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public abstract class BaseCommand implements Runnable {
	static final String UTF8 = "UTF8";

	CommandType type;
	Socket socket;
	InputStream in;
	OutputStream out;
	boolean hasParameters = false;
	
	public BaseCommand(CommandType type, Socket socket) {
		this(type,socket,true);
	}
	public BaseCommand(CommandType type, Socket socket, boolean hasParameters) {
		this.type = type;
		this.socket = socket;
		this.hasParameters = hasParameters;
	}

	@Override
	public void run() {
		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();
			//1. send preamble (command name)
			writePreamble();
			//2. send command specific variables?
			if(hasParameters) {
				sendParameters(out);
			}
			//in any case, send the bt.address parameter
			sendParameter( 
				"bt.address", 
				dk.itu.android.bluetooth.BluetoothAdapter.getDefaultAdapter().getAddress(), 
				out );
			out.write("]".getBytes(UTF8));
			//3. close input stream
//			out.close();
			//4. read response
			//5. read >>>>EOF<<<<
			readResponse(in);
			//6. close output stream and socket.
//			out.close();
//			in.close();
			socket.close();
			Log.d("BTEMU_CMD", "command execution completed.");
		} catch(Exception e) {
			Log.e("BTEMU_CMD", "error while executing command " + type.name(), e);
		} finally {
			if(socket.isConnected()) {
				try{socket.close();}catch(Exception ignored){}
			}
		}
	}
	
	protected void writePreamble() throws IOException {
		Log.d("BTEMU_CMD", "writing preamble for type: "+type.name());
		out.write( (Integer.toString(type.intRepr)+"]").getBytes(UTF8) );
	}
	
	protected abstract void sendParameters(OutputStream out) throws IOException;
	protected abstract void readResponse(InputStream in) throws IOException;
	
	protected void sendParameter(String name, String value, OutputStream out) throws IOException {
		out.write( (name+"="+value+"&").getBytes(UTF8) );
	}
}
