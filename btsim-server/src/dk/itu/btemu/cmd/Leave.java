package dk.itu.btemu.cmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import dk.itu.btemu.State;

public class Leave extends BaseCommand {
	
	public Leave(Socket socket, InputStreamReader socketInputStream) {
		super(socket, socketInputStream);
	}

	@Override
	protected void work(List<Param> params) throws Exception {
		State.getInstance().remove(getParam("bt.address"));
	}

	@Override
	protected void writeResponse(OutputStream out) throws IOException {
	}

}
