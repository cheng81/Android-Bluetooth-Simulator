package dk.itu.btemu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static String ADB_CMD_PATH = "";//Applications/code/android-sdk/tools/";
	public static String ADB_EXE = "adb";
	public static String ADB_CMD(){ return ADB_CMD_PATH + ADB_EXE; }
	static int StartPort = 8123;
	static int _n = 0;
	public static int next() {
		_n++;
		return StartPort+_n;
	}

	int port;
	boolean running;
	CommandParser cmdParser;
	Thread serverThread;
	ServerSocket server;
	
	public Server(int port) {
		running = true;
		this.port = port;
		this.cmdParser = new CommandParser();
	}
	
	public void start() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		serverThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(running) {
					try {
						Socket spawned = server.accept();
						System.out.println("accepting a new connection from "
								+ spawned.getInetAddress());
						spawnWorker(spawned);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("error in server main loop!");
					}
				}
			}
		});
		serverThread.start();
	}
	public void stop() {
		if(serverThread != null) {
			running = false;
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void spawnWorker(final Socket socket) throws IOException {
		new Thread(cmdParser.resolve(socket)).start();
	}
	
	public static void main(String args[]) throws Exception {
		int port = 8199;
		if(args.length>0) {
			if(args[0].equals("--help")) {
				PrintHelp();
				return;
			} else {
				for(String s : args) {
					if(s.startsWith("--port=")) {
						String sport = s.substring("--port=".length());
						port = Integer.parseInt(sport);
					} else if(s.startsWith("--adb.path=")) {
						ADB_CMD_PATH = s.substring("--adb.path".length());
					}
				}
			}
		}
		if( System.getProperty("os.name").toLowerCase().indexOf("win") >= 0 ) {
			//this is windows, so the ADB_EXE is adb.exe
			ADB_EXE = "adb.exe";
		}
		
		Server s = new Server(port);
		s.start();
		
		System.out.println("Server started on port " + s.port);
		System.out.println("press any key to exit");
		System.in.read();
		System.out.println("exiting...");
		s.stop();
	}
	
	private static void PrintHelp() {
		String out = "Start the Bluetooth Android Emulator server.\n";
		out += "Sample usage:\n";
		out += "java dk.itu.btemu.Server --adb.path=/usr/local/share/android.sk/tools/\n\n";
		out += "Parameters:\n";
		out += "--port=<port> specify the port on which run the server\n";
		out += "\tdefault: 8199. Note that currently is hard-coded in\n";
		out += "\tthe android library.\n";
		out += "--adb.path=<path> specify where to find the adb command\n";
		out += "\tdefault: /Applications/code/android.sdk/tools/\n\n";
		out += "If you want to check which devices are connected, \n";
		out += "telnet localhost <port>, followd by the discovery command\n";
		out += "which is 2]](return)";
		System.out.println(out);
	}
}
