package dk.itu.android.bluetooth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import dk.itu.android.btemu.service.BTEmulator;
import dk.itu.android.btemu.service.cmd.Discovery;

public class BluetoothAdapter {
	public static final String ACTION_DISCOVERY_FINISHED = "dk.android.bluetooth.adapter.action.DISCOVERY_FINISHED";
	public static final String ACTION_DISCOVERY_STARTED = "dk.android.bluetooth.adapter.action.DISCOVERY_STARTED";
	/**
	 * Contains EXTRA_LOCAL_NAME with the new name
	 */
	public static final String ACTION_LOCAL_NAME_CHANGED = "dk.android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
	/**
	 * Activity Action: Show a system activity that requests discoverable mode. This activity will also request the user to turn on Bluetooth if it is not currently enabled.
Discoverable mode is equivalent to SCAN_MODE_CONNECTABLE_DISCOVERABLE. It allows remote devices to see this Bluetooth adapter when they perform a discovery.
For privacy, Android is not discoverable by default.
The sender of this Intent can optionally use extra field EXTRA_DISCOVERABLE_DURATION to request the duration of discoverability. Currently the default duration is 120 seconds, and maximum duration is capped at 300 seconds for each request.
Notification of the result of this activity is posted using the onActivityResult(int, int, Intent) callback. The resultCode will be the duration (in seconds) of discoverability or RESULT_CANCELED if the user rejected discoverability or an error has occurred.
Applications can also listen for ACTION_SCAN_MODE_CHANGED for global notification whenever the scan mode changes. For example, an application can be notified when the device has ended discoverability.
Requires BLUETOOTH
	 */
	public static final String ACTION_REQUEST_DISCOVERABLE = "dk.android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
	/**
	 * Activity Action: Show a system activity that allows the user to turn on Bluetooth.
This system activity will return once Bluetooth has completed turning on, or the user has decided not to turn Bluetooth on.
Notification of the result of this activity is posted using the onActivityResult(int, int, Intent) callback. The resultCode will be RESULT_OK if Bluetooth has been turned on or RESULT_CANCELED if the user has rejected the request or an error has occurred.
Applications can also listen for ACTION_STATE_CHANGED for global notification whenever Bluetooth is turned on or off.
Requires BLUETOOTH
	 */
	public static final String ACTION_REQUEST_ENABLE = "dk.android.bluetooth.adapter.action.REQUEST_ENABLE";
	/**
	 * Broadcast Action: Indicates the Bluetooth scan mode of the local Adapter has changed.
Always contains the extra fields EXTRA_SCAN_MODE and EXTRA_PREVIOUS_SCAN_MODE containing the new and old scan modes respectively.
Requires BLUETOOTH
	 */
	public static final String ACTION_SCAN_MODE_CHANGED = "dk.android.bluetooth.adapter.action.SCAN_MODE_CHANGED";
	/**
	 * Broadcast Action: The state of the local Bluetooth adapter has been changed.
For example, Bluetooth has been turned on or off.
Always contains the extra fields EXTRA_STATE and EXTRA_PREVIOUS_STATE containing the new and old states respectively.
Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_STATE_CHANGED = "dk.android.bluetooth.adapter.action.STATE_CHANGED";
	/**
	 * Sentinel error value for this class. Guaranteed to not equal any other integer constant in this class. Provided as a convenience for functions that require a sentinel error value, for example:
Intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
	 */
	public static final int ERROR = -2147483648;
	/**
	 * Used as an optional int extra field in ACTION_REQUEST_DISCOVERABLE intents to request a specific duration for discoverability in seconds. The current default is 120 seconds, and requests over 300 seconds will be capped. These values could change.
	 */
	public static final String EXTRA_DISCOVERABLE_DURATION = "dk.android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";
	/**
	 * Used as a String extra field in ACTION_LOCAL_NAME_CHANGED intents to request the local Bluetooth name.
	 */
	public static final String EXTRA_LOCAL_NAME = "dk.android.bluetooth.adapter.extra.LOCAL_NAME";
	/**
	 * Used as an int extra field in ACTION_SCAN_MODE_CHANGED intents to request the previous scan mode. Possible values are: SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE,
	 */
	public static final String EXTRA_PREVIOUS_SCAN_MODE = "dk.android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";
	/**
	 * Used as an int extra field in ACTION_STATE_CHANGED intents to request the previous power state. Possible values are: STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF,
	 */
	public static final String EXTRA_PREVIOUS_STATE = "dk.android.bluetooth.adapter.extra.PREVIOUS_STATE";
	/**
	 * Used as an int extra field in ACTION_SCAN_MODE_CHANGED intents to request the current scan mode. Possible values are: SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE,
	 */
	public static final String EXTRA_SCAN_MODE = "dk.android.bluetooth.adapter.extra.SCAN_MODE";
	/**
	 * Used as an int extra field in ACTION_STATE_CHANGED intents to request the current power state. Possible values are: STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF,
	 */
	public static final String EXTRA_STATE = "dk.android.bluetooth.adapter.extra.STATE";

	public static final int SCAN_MODE_CONNECTABLE = 21;
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;
	public static final int SCAN_MODE_NONE = 20;
	public static final int STATE_OFF = 10;
	public static final int STATE_ON = 12;
	public static final int STATE_TURNING_OFF = 13;
	public static final int STATE_TURNING_ON = 11;
	
	static final BluetoothAdapter def = new BluetoothAdapter();
	
	static Context context = null;
	public static void SetContext(Context c) {
		if(context != null) return;
		context=c;
		try {
			InputStream is = c.getContentResolver().openInputStream(Uri.parse("file:///data/data/dk.itu.android.btemu/files/BTADDR.TXT"));
			Reader reader = new InputStreamReader(is);
			char[] buf = new char[100];
			int read = reader.read(buf);
			String addr = new String(buf,0,read);
			def.addr = addr;
			Log.i("BTADAPTEREMULATOR", "read address: "+def.addr);
			return;
		} catch(Exception e) {
			Log.i("BTADAPTEREMULATOR", "file was not found", e);
		}
		try {
//			for(String name : c.fileList()){
//				if(name.equals("BTADDR.TXT")) {
//					Log.i("BTADAPTEREMULATOR", "btaddress found");
//					FileInputStream fis = c.openFileInput("BTADDR.TXT");
//					return;
//				}
//			}
			FileOutputStream fos = c.openFileOutput("BTADDR.TXT", Context.MODE_WORLD_READABLE);
			OutputStreamWriter outw = new OutputStreamWriter(fos);
			outw.write(def.getAddress());
			outw.flush();
			outw.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("BTADAPTEREMULATOR", "cannot write BTADDR.TXT file!", e);
		}
	}
	
	String name = "local";
	String addr = null;
	boolean enabled = false;
	boolean discovering = false;
	Set<BluetoothDevice> bonded = new HashSet<BluetoothDevice>();
	int scanMode = android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
	
	public static boolean checkBluetoothAddress(String addr) {
		return android.bluetooth.BluetoothAdapter.checkBluetoothAddress(addr);
	}
	public static BluetoothAdapter getDefaultAdapter() {
		return def;
	}
	
	private BluetoothAdapter() {
		this.addr = getAddress();
		this.emulator = new BTEmulator();
	}
	
	public boolean cancelDiscovery() {
		return false;
	}
	public boolean disable(){
		return setEnabled(false);
	}
	public boolean enable(){
		return setEnabled(true);
	}
	public String getAddress(){
		/**
		 * TODO: get a decent random address!
		 */
		if(addr==null){addr=createRandomAddress();}
		return addr;
	}
	public Set<BluetoothDevice> getBondedDevices(){
		Set<BluetoothDevice> out = null;
		synchronized(this.bonded) {
			out = new HashSet<BluetoothDevice>(bonded);
		}
		return out;
	}
	public String getName(){
		return name;
	}
	public BluetoothDevice getRemoteDevice(String address) {
		if(!checkBluetoothAddress(address))
			throw new IllegalArgumentException("wrong device address");
		return emulator.lookupBT(address);//new BluetoothDevice(address);
	}
	public int getScanMode() {
		return scanMode;
	}
	public boolean isDiscovering() {
		return discovering;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid)
	throws IOException {
		
		//chhoose a random tcp port
		int port = choosePort();
		
		BluetoothServerSocket out = new BluetoothServerSocket(emulator,uuid.toString(),port);
		
//		emulator.addService(uuid.toString(), port);
		
		return out;
	}
	public boolean setName(String name) {
		this.name = name;
		return true;
	}
	public boolean startDiscovery() {
		if(discovering)
			return false;
		
		discovering = true;
		Discovery.WithDevices wd = new Discovery.WithDevices() {
			@Override
			public void devices(List<BluetoothDevice> devices) {
				Intent intent = new Intent(ACTION_DISCOVERY_STARTED);
				context.sendBroadcast(intent);
				for(BluetoothDevice d : devices) {
					intent = new Intent();
					intent.setAction(BluetoothDevice.ACTION_FOUND);
					intent.putExtra(BluetoothDevice.EXTRA_DEVICE, d);
					intent.putExtra(BluetoothDevice.EXTRA_CLASS, d.getBluetoothClass());
					context.sendBroadcast(intent);
				}
				intent = new Intent(ACTION_DISCOVERY_FINISHED);
				context.sendBroadcast(intent);
				discovering = false;
			}
		};
		emulator.asyncDiscovery(wd);
		return true;
	}
	
	
	
	
	/////////////////////////////
	private BTEmulator emulator;
	private boolean setEnabled(boolean enable) {
		Log.d("BTADAPTEREMULATOR", "setEnabled->"+enable+", was->"+enabled);
		if(enable){// && !enabled) {
			Log.d("BTADAPTEREMULATOR", "joining...");
			emulator.join();
			
		} else if(/*enabled && */!enable) {
			Log.d("BTADAPTEREMULATOR", "leaving...");
			emulator.leave();
		}
		this.enabled = enable;
		return true;
	}
	private String createRandomAddress() {
		//sample: 00:11:22:AA:BB:CC
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		boolean f = true;
		for(int i=0;i<3;i++) {
			if(f){f=!f;}else{sb.append(":");}
			int n = 0;
			do {
				n = r.nextInt(99);
			} while(n<10);
			sb.append(n);
		}
		String chars = "ABCDEF";
		for(int i=0;i<3;i++) {
			sb.append(":");
			sb.append( chars.charAt( r.nextInt(6) ) );
			sb.append( chars.charAt( r.nextInt(6) ) );
		}
		
		return sb.toString();
	}
	static int _curPort = 8123;
	private int choosePort() {
		_curPort++;
		return _curPort;
	}
}
