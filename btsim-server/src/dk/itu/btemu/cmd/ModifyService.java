package dk.itu.btemu.cmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import dk.itu.btemu.State;
import dk.itu.btemu.state.Device;

public class ModifyService extends BaseCommand {

	public ModifyService(Socket socket, InputStreamReader socketInputStream) {
		super(socket, socketInputStream);
	}

	@Override
	protected void work(List<Param> params) throws Exception {
		String sport = getParam("tcp.port");
		String UUID = getParam("service.uuid");
		String type = getParam("type");
		int port = Integer.parseInt(sport);
		
		Device d = State.getInstance().get(getParam("bt.address"));
		
		if(type.equals("added")) {
			d.addService(UUID, port);
		} else {
			d.removeService(UUID);
		}
	}

	@Override
	protected void writeResponse(OutputStream out) throws IOException {
	}

}
