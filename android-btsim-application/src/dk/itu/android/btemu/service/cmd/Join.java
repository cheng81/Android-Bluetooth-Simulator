package dk.itu.android.btemu.service.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Join extends BaseCommand {
	
	public Join( Socket socket ) {
		super(CommandType.JOIN, socket);
	}

	@Override
	protected void readResponse(InputStream in) throws IOException {
		//ok, we don't really care about the response for join
	}

	@Override
	protected void sendParameters(OutputStream out) throws IOException {
		sendParameter( "tcp.address","10.0.2.2",out );
		sendParameter( "device.name", dk.itu.android.bluetooth.BluetoothAdapter.getDefaultAdapter().getName(), out);
//		sendParameter( "bt.address", dk.itu.android.bluetooth.BluetoothAdapter.getDefaultAdapter().getAddress(), out );
	}

}
