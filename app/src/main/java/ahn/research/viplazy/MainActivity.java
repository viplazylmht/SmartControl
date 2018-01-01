package ahn.research.viplazy;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.widget.AdapterView.*;

public class MainActivity extends Activity 
{
	//private static final int DELETE_WORK = Menu.FIRST;
	//private static final int ABOUT = Menu.FIRST + 2;
    Button btnPaired;
    ListView devicelist;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

	private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
			msg("Start ledControl at address: " + address);
            // Make an intent to start next activity.
			Intent i = new Intent(MainActivity.this, ledControl.class);
			//Intent i = new Intent(DeviceList.this, MainActivity.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity;

            startActivity(i);
        }
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
		
		msg("Main Class Here");
        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);
		devicelist.setClickable(false);
        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Rất tiếc, thết bị không hỗ trợ Bluetooth", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
			//Ask to the user turn the bluetooth on
			Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					pairedDevicesList();
				}
			});
		
    }
	/*@Override
	private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
		@Override
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
			msg("Start ledControl at address: " + address);
            // Make an intent to start next activity.
			Intent i = new Intent(MainActivity.this, ledControl.class);
			//Intent i = new Intent(DeviceList.this, MainActivity.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity;
			
            startActivity(i);
        }
    };*/
	private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Không tìm thấy thiết bị, vui lòng dò và kết nối thủ công.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }
	
	private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    };
	
}
