package dk.itu.btemu.cmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import dk.itu.btemu.State;
import dk.itu.btemu.state.Device;
import dk.itu.btemu.state.Service;

public class Discovery extends BaseCommand {

	List<Device> devices;
	String btAddr;
	public Discovery(Socket socket, InputStreamReader socketInputStream) {
		super(socket, socketInputStream);
	}

	@Override
	protected void work(List<Param> params) throws Exception {
		devices = State.getInstance().asList(Device.class);
		btAddr = getParam("bt.address");
	}

	@Override
	protected void writeResponse(OutputStream out) throws IOException {
//		OutputStreamWriter wrt = new OutputStreamWriter(out,"UTF-8");
		for( Device d : devices ) {
			if(d.getBtAddr().equals(btAddr))
				continue;
			String cur = d.getBtAddr() + "--" + d.getIpAddr() + "--";
			boolean f = true;
			for(Service s : d.getServices()) {
				if(f){f=!f;}else{cur+="<><>";}
				cur += s.getUUID() + "<>" + s.getHostPort();
			}
			cur+="\r\n";
			out.write(cur.getBytes("UTF-8"));
			System.out.println("writing: " + cur);
		}
	}

}
