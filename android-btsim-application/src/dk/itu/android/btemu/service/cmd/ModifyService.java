package dk.itu.android.btemu.service.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ModifyService extends BaseCommand {

	int port;
	String uuid;
	boolean added;
	public ModifyService(Socket socket, String uuid, int port, boolean added) {
		super(CommandType.MODIFYSERVICE, socket);
		this.uuid = uuid;
		this.port = port;
		this.added = added;
	}

	@Override
	protected void readResponse(InputStream in) throws IOException {
	}

	@Override
	protected void sendParameters(OutputStream out) throws IOException {
		sendParameter("type", added ? "added" : "removed", out);
		sendParameter("tcp.port", port+"",out);
		sendParameter("service.uuid",uuid,out);
	}

}
