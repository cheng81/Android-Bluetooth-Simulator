package dk.itu.android.btemu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothDevice;
import dk.itu.android.bluetooth.BluetoothServerSocket;
import dk.itu.android.bluetooth.BluetoothSocket;

public class Main extends Activity {
	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				addDevice( (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) );
			}
		}
	};
	static final String ITEM_KEY = "key";
	ListView devices;
	BluetoothAdapter bta;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	BluetoothAdapter.SetContext(this);
        bta = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		
		Button enableBtn = (Button)findViewById(R.id.Enable);
		Button disableBtn = (Button)findViewById(R.id.Disable);
		Button discoveryBtn = (Button)findViewById(R.id.Discovery);
		
		Button serverBtn = (Button)findViewById(R.id.StartServer);
		Button clientBtn = (Button)findViewById(R.id.StartClient);
		serverBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startServer();
			}
		});
		clientBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startClient();
			}
		});
		
		devices = (ListView)findViewById(R.id.Devices);
//		devices.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}
//		});
		devices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.i("BTEMU_TEST", "selecting remote device at position: "+position);
				other = (BluetoothDevice)list.get(position).get("DEVICE");
				Log.i("BTEMU_TEST", "other device is: "+other);
			}
		});
		
		enableBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enableBT();
			}
		});
		disableBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				disableBT();
			}
		});
		discoveryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startDiscovery();
			}
		});
		
		adapter = new SimpleAdapter(this, list, R.layout.row, new String[]{ITEM_KEY}, new int[]{R.id.list_value});
		this.devices.setAdapter(adapter);
    }
    SimpleAdapter adapter;
	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    
	static int REQUEST_ENABLE_BT = 1234;
    private void enableBT() {
    	Log.i("BTEMU", "enabling btemulator...");
//    		bta.enable();
    	if(!bta.isEnabled()) {
    		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_ENABLE_BT) {
    		//just received the results for bt enabling
    		if(resultCode == RESULT_OK) {
    			Log.i("BTEMU", "bluetooth was enabled!");
    		}
    	}
    }
    private void disableBT() {
    	Log.i("BTEMU", "disabling btemulator...");
    		bta.disable();
    }
    private void startDiscovery() {
    	Log.i("BTEMU", "starting discovery...");
    	clearDevices();
    	bta.startDiscovery();
    }
    
    private void clearDevices(){
    	Log.i("BTEMU", "clearing device list");
    	list.clear();
    	adapter.notifyDataSetChanged();
    }
    BluetoothDevice other = null;
    @SuppressWarnings("serial")
	private void addDevice(final BluetoothDevice d) {
//    	other = d;
    	Log.i("TESTACTIVITY", "got device! " + d.getAddress());
    	Map<String,Object> item = new HashMap<String,Object>(){{
    		put(ITEM_KEY, d.getName() + " - " + d.getAddress());
    		put("DEVICE", d);
    	}};
    	list.add(item);
    	adapter.notifyDataSetChanged();
    }
    
    private void startServer() {
    	new Thread(new Runnable(){
    		@Override
    		public void run() {
    	    	try {
    	    		Log.i("BTTEST", "accepting bt connection...");
    				BluetoothServerSocket server = BluetoothAdapter
    					.getDefaultAdapter()
    					.listenUsingRfcommWithServiceRecord("dk.echo", 
    							UUID.fromString("419bbc68-c365-4c5e-8793-5ebff85b908c"));
    				BluetoothSocket client = server.accept();
    				String line = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
    	    		Log.i("BTTEST", "read: "+line);
    				client.getOutputStream().write( ("echoed: "+line).getBytes("UTF-8") );
    	    		Log.i("BTTEST", "replying");
    				client.close();
    				server.close();
    			} catch (Exception e) {
    				Log.e("BTTEST", "error in echo server", e);
    			}
    		}
    	}).start();
    }
    String resp;
    private void setResp(String r){resp=r;}
    private void startClient() {
    	Thread t =new Thread(new Runnable(){
    		@Override
    		public void run() {
    	    	try {
    	    		Log.i("BTTEST_CLIENT", "creating socket..");
    				BluetoothSocket s = other.createRfcommSocketToServiceRecord(UUID.fromString("419bbc68-c365-4c5e-8793-5ebff85b908c"));
    	    		Log.i("BTTEST_CLIENT", "socket created");
    	    		s.connect();
    	    		Log.i("BTTEST_CLIENT", "socket connected");
    				s.getOutputStream().write("foo bar? baaaz\r\n".getBytes("UTF-8"));
    				s.getOutputStream().flush();
    				Log.i("BTTEST_CLIENT", "message wrote");
    				String reply = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();
    	    		Log.i("BTTEST_CLIENT", "reply: "+reply);
    	    		setResp(reply);
    				s.close();
    				runOnUiThread(new Runnable(){
    					@Override
    					public void run() {
    						((TextView)findViewById(R.id.EchoResponse)).setText(resp);
    					}
    				});
//    				synchronized(lock) {
//    					lock.notify();
//    				}
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				Log.e("BTTEST", "error in echo client", e);
    			}
    		}
    	});
    	t.start();
//    	try {
//			
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	synchronized(lock) {
//    		try {
//    			lock.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//		}
    }
}