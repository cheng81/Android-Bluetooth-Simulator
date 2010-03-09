package dk.itu.android.btemu.service.cmd;

public enum CommandType {

	JOIN(0),
	LEAVE(1),
	DISCOVERY(2),
	MODIFYSERVICE(3)
	;
	
	int intRepr;
	private CommandType(int intRepr) {
		this.intRepr = intRepr;
	}
}
