package dk.itu.android.btemu.service;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.util.Log;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.btemu.service.cmd.Discovery;
import dk.itu.android.btemu.service.cmd.Join;
import dk.itu.android.btemu.service.cmd.Leave;
import dk.itu.android.btemu.service.cmd.ModifyService;

public class BTEmulator {
	static final String TAG = "BTEMULATOR";

	static final String DevMachineIP = "10.0.2.2";
	static final int DiscoveryServerPort = 8199;
	
	static final BTEmulator _instance = new BTEmulator();
	public static BTEmulator instance(){ return _instance; }
	
	public BTEmulator() {
	}
	
	public void join() {
		try {
			new Join(new Socket(DevMachineIP,DiscoveryServerPort)).run();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot join", e);
		}
	}
	public void leave() {
		try {
			new Leave(new Socket(DevMachineIP,DiscoveryServerPort)).run();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot leave", e);
		}
	}
	
	public void asyncDiscovery( Discovery.WithDevices wd ) {
		Discovery d;
		try {
			d = new Discovery(new Socket(DevMachineIP,DiscoveryServerPort));
			d.setWithDevices(wd);
			new Thread(d).start();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot async discovery", e);
		}
	}
	
	public void discovery( Discovery.WithDevices wd ) {
		Discovery d;
		try {
			d = new Discovery(new Socket(DevMachineIP,DiscoveryServerPort));
			d.setWithDevices(wd);
			d.run();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot discovery", e);
		}
	}
	
	public BluetoothDevice lookupBT( String btAddr ) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		FutureTask<List<BluetoothDevice>> future = 
			new FutureTask<List<BluetoothDevice>>(new Callable<List<BluetoothDevice>>(){
			@Override
			public List<BluetoothDevice> call() throws Exception {
				Discovery d = new Discovery(new Socket(DevMachineIP,DiscoveryServerPort));
				d.run();
				return d.getDevices();
			}
		});
		executor.execute(future);
		try {
			List<BluetoothDevice> devices = future.get();
			for(BluetoothDevice d : devices) {
				Log.i(TAG, "check btaddr: "+d.getAddr() + " == " + btAddr + "?");
				if(d.getAddr().equals(btAddr)) {
					Log.i(TAG, "btAddr match, return device");
					return d;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot retrieve btdevices", e);
		}
		Log.i(TAG, "btAddr " + btAddr + " not found! return null!");
		return null;
	}
//	public BluetoothDevice lookupIP( String ipAddr ) {
//		ExecutorService executor = Executors.newFixedThreadPool(1);
//		FutureTask<List<BluetoothDevice>> future = 
//			new FutureTask<List<BluetoothDevice>>(new Callable<List<BluetoothDevice>>(){
//			@Override
//			public List<BluetoothDevice> call() throws Exception {
//				Discovery d = new Discovery(new Socket(DevMachineIP,DiscoveryServerPort));
//				d.run();
//				return d.getDevices();
//			}
//		});
//		executor.execute(future);
//		try {
//			List<BluetoothDevice> devices = future.get();
//			for(BluetoothDevice d : devices) {
//				if(d.getTcpAddr().equals(ipAddr))
//					return d;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e(TAG, "cannot retrieve btdevices", e);
//		}
//		
//		return null;
//	}
	
	public void addService( String uuid, int port ) {
		modifyService(uuid,port,true);
	}
	public void removeService( String uuid, int port ) {
		modifyService(uuid,port,false);
	}
	protected void modifyService( String uuid, int port, boolean add ) {
		try {
			new ModifyService(new Socket(DevMachineIP,DiscoveryServerPort),uuid,port,add).run();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "cannot modify service", e);
		}
	}
}
