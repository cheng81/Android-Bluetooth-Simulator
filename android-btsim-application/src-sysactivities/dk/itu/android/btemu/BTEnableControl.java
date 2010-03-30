package dk.itu.android.btemu;

import dk.itu.android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BTEnableControl extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.ctrl);
        
        BluetoothAdapter.SetContext(this);
	}

	@Override
	protected void onStart() {
        super.onStart();
		Intent started = getIntent();
		Log.i("BTCTRL", "start with action: "+started.getAction());
		if(started.getAction().equals(BluetoothAdapter.ACTION_REQUEST_ENABLE)) {
			//start
			BluetoothAdapter.getDefaultAdapter().enable();
			setResult(RESULT_OK);
			
		} else if(started.getAction().equals(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)) {
			//enabled discoverable
			setResult(RESULT_OK);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}
	
}
