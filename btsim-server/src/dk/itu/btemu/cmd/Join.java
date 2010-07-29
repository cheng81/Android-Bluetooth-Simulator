package dk.itu.btemu.cmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import dk.itu.btemu.BTMacForwardingsChecker;
import dk.itu.btemu.State;
import dk.itu.btemu.state.Device;

public class Join extends BaseCommand {
	int port = -1;

	public Join(Socket s, InputStreamReader in) {
		super(s,in);
	}

	@Override
	protected void work( List<Param> params ) throws Exception {
		String ipAddress = getParam("tcp.address");//remoteIp;//this.socket.getInetAddress().getHostAddress();
		String btAddress = getParam("bt.address");
		
		//save them
		Device device = new Device(ipAddress, btAddress);
		State.getInstance().put(btAddress, device);
		
		
		if(null == getParam("not.android.emulator")) {
			BTMacForwardingsChecker checker = new BTMacForwardingsChecker();
			System.out.println("running checker...");
			checker.run();
			port = device.getEmulatorPort();
		} else {
			device.setAndroidEmulator(false);
		}
	}

	@Override
	protected void writeResponse(OutputStream out) throws IOException {
		if(port > 0) {
			out.write( (port+"\n").getBytes("UTF-8") );
		}
	}
	
	
}
