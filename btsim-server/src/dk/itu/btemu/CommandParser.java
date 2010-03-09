package dk.itu.btemu;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import dk.itu.btemu.cmd.BaseCommand;
import dk.itu.btemu.cmd.CommandType;

public class CommandParser {
	public BaseCommand resolve(Socket socket) throws IOException {
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		
		char[] buf = new char[1];
		
		StringBuilder sb = new StringBuilder();
		String s = "";
		int count = 0;
		
		do {
			count = isr.read(buf);
			sb.append(buf);
			s = sb.toString();
			System.out.println("new string is : '" + s + "'");
		} while( s.charAt(s.length()-1) != ']' && count != -1 );
		
		s = s.substring(0, s.length()-1);
		int type = Integer.parseInt(s);
		
		return CommandType.fromRepr(type).createCommand(socket, isr);
	}
}
