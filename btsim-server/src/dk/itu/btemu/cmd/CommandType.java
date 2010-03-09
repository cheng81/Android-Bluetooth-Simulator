package dk.itu.btemu.cmd;

import java.io.InputStreamReader;
import java.net.Socket;

public enum CommandType {

	JOIN(0, Join.class),
	LEAVE(1, Leave.class),
	DISCOVERY(2, Discovery.class),
	MODIFYSERVICE(3, ModifyService.class);
	
	int intRepr;
	Class<? extends BaseCommand> cls;
	private CommandType(int i, Class<? extends BaseCommand> cls) {
		intRepr = i;
		this.cls = cls;
	}
	@SuppressWarnings("unchecked")
	public <T extends BaseCommand> T createCommand(Socket s, InputStreamReader r) {
		System.out.println("creating command " + name());
		try {
			return (T)cls.getConstructor(Socket.class,InputStreamReader.class).newInstance(s,r);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static CommandType fromRepr(int i) {
		for(CommandType c : values()) {
			if(i==c.intRepr)
				return c;
		}
		return null;
	}
}
