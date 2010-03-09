package dk.itu.android.btemu.service.cmd;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class NoParamsBaseCommand extends BaseCommand {

	public NoParamsBaseCommand(CommandType type, Socket socket) {
		super(type,socket,false);
	}
	
	@Override
	protected void sendParameters(OutputStream in) throws IOException {
		//emtpy method
	}

}
