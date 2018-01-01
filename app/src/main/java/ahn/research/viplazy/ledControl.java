package ahn.research.viplazy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;
import android.widget.*;
import android.app.*;
import android.content.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import java.util.concurrent.*;
import android.os.*;


public class ledControl extends Activity {
	private static final int DELETE_WORK = Menu.FIRST;
	private static final int ABOUT = Menu.FIRST + 2;
	private static final int SAVE = Menu.FIRST + 4;
	private static final int LOAD = Menu.FIRST + 6;
	
    Button btnSend, btnRef, btnDis, button;
    EditText workEnter, time_delay;
	ListView list;
	Handler bluetoothIn;
	private StringBuilder recDataString = new StringBuilder();
	private ConnectedThread mConnectedThread;
	final int handlerState = 0;   
    TextView  btnTest,txtString;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private ListWorkAdapter arrayAdapter;

	private ArrayList<Work> array;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.main);
		list =(ListView)findViewById(R.id.list);
        //call the widgtes
        array = new ArrayList<Work>();
		arrayAdapter = new ListWorkAdapter(this, R.layout.list, array);
		list.setAdapter(arrayAdapter);
		
		workEnter =(EditText)findViewById(R.id.work_enter);
		time_delay=(EditText)findViewById(R.id.time_delay);
		txtString=(TextView)findViewById(R.id.view_stt);
		button=(Button)findViewById(R.id.button);
		btnDis=(Button)findViewById(R.id.btn_dis);
		btnSend=(Button)findViewById(R.id.btn_send);
		btnRef=(Button)findViewById(R.id.btn_ref);
		bluetoothIn = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == handlerState) {										//if message is what we want
					String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
					recDataString.append(readMessage);      								//keep appending to string until ~
					int endOfLineIndex = recDataString.indexOf("~");        // determine the end-of-line
					int startOfLineIndex = recDataString.indexOf("#");    
					if (endOfLineIndex > 0) {                                           // make sure there data before ~
						String dataInPrint = recDataString.substring(startOfLineIndex+1, endOfLineIndex);    // extract string
						txtString.setText(dataInPrint);           		
						//xuLyDuLieu(dataInPrint);
						//recDataString = new StringBuilder();
						recDataString.delete(0, recDataString.length()); 					//clear all string data 
						
						dataInPrint = "";
					}            
				}
			}
		};
		
      //  brightness = (SeekBar)findViewById(R.id.seekBar);
     //   lumn = (TextView)findViewById(R.id.lumn);

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
		
        
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: Implement this method
				ThemDuLieu();
			}
		});
		btnDis.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View p1)
			{
				// TODO: Implement this method
				Disconnect();
			}
		});
		btnSend.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					turnOnLed();
				}
			});
		btnRef.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					dongbo();
				}
			});
    }
	/* ==     ==       ==     ==    */
	private void ThemDuLieu()
    {
        if (workEnter.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ledControl.this);
			builder.setTitle("Lỗi");
			builder.setMessage("Chưa nhập tên lịch trình");
			builder.setPositiveButton("Thử lại", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
			builder.show();
		}
		else {

			Calendar mcurrentTime = Calendar.getInstance();
			int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
			int minute = mcurrentTime.get(Calendar.MINUTE);
			TimePickerDialog mTimePicker;
			mTimePicker = new TimePickerDialog(ledControl.this, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
						//    time.setText(selectedHour + ":" + selectedMinute);
						String gio = selectedHour + "";
						String phut = selectedMinute + "";
						String workContent = workEnter.getText().toString();
						//String timeContent = gio + " gio " + phut + " phut";
						String t_delay = time_delay.getText().toString();
						Work work = new Work(workContent, gio, phut,t_delay);
						array.add(0, work);
						arrayAdapter.notifyDataSetChanged();
						workEnter.setText("");
						time_delay.setText("");
						//hourEdit.setText("");
						//minuteEdit.setText("");
					}
				}, hour, minute, true);            // Dung dinh dang 24h
			mTimePicker.setTitle("Chọn Thời Gian");
			mTimePicker.show(); 

		}

    }
	public void save(String fileName) throws FileNotFoundException {
		FileOutputStream fout= new FileOutputStream (fileName);
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(array);
			fout.close();
		}
		catch (IOException e)
		{ msg("Error while saving data");}
	
	}
	//To read back you can have

	public void read(String fileName) throws FileNotFoundException {
		FileInputStream fin= new FileInputStream (fileName);
		try
		{
			ObjectInputStream ois = new ObjectInputStream(fin);
			try
			{
				array = (ArrayList<Work>)ois.readObject();
			}
			catch (ClassNotFoundException e)
			{}
			catch (IOException e)
			{}
			fin.close();
		}
		catch (IOException e)
		{}
		
	}
	private void xuLyDuLieu(String inp) {
		String[] recv = new String[30];
		int k=0;
		
		StringBuilder str = new StringBuilder(inp);
		txtString.setText("Prepare: " + str);
		int end= inp.indexOf(" ");
		while (end > 0) {
			recv[k] = inp.substring(0,end);
			str.delete(0,end+1);
			k++;
		}
		//   =========
		switch (recv[0]) {
			case "clear": {
				array.clear();
				arrayAdapter.notifyDataSetChanged();
				break;
			}
			case "add": {
				Work in = new Work("NONAME", recv[1], recv[2],recv[3]);
				array.add(0, in);
				arrayAdapter.notifyDataSetChanged();
				break;
			}
			case "LOG:": {
				String s = "";
				for (int i=1;i<=30;i++) if (recv[i].equals("")==false) {
					s += recv[i] + " ";
				}
				txtString.setText(s);
				break;
			}
			default: msg("Null Command~~");
		}
	}

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Lỗi");}
        }
        finish(); //return to the first layout

    }

    private void dongbo()
    {
        if (btSocket!=null)
        {
            try
            {
				DateFormat df = new SimpleDateFormat("HH mm ss");
				String date = df.format(Calendar.getInstance().getTime());
				String tg = "#sync " + date + "~";
                btSocket.getOutputStream().write(tg.getBytes());
				tg = "#time~";
                btSocket.getOutputStream().write(tg.getBytes());
				btSocket.getOutputStream().write("#get~".toString().getBytes());
				//recvGet(btSocket, array);
            }
            catch (IOException e)
            {
                msg("Lỗi");
            }
        }
    }
	
    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("#clear~".getBytes());
				for (int i=0;i<array.size();i++)
				{
					String tg = "#set " + array.get(i).getTimeh() + " " + array.get(i).getTimem() + " " + array.get(i).getDelay() + "~";
					btSocket.getOutputStream().write(tg.getBytes());
					msg("Nạp thành công!");
				}
            }
            catch (IOException e)
            {
                msg("Lỗi");
            }
        }
    }
	private void deleteCheckedWork() {
		if (array.size() > 0) {
			int i=0;
			while (i < array.size()) {
				if (i > array.size()) {
					break;
				}
				if (array.get(i).isChecked()) {
					array.remove(i);
					arrayAdapter.notifyDataSetChanged();
					continue;
				}
				i++;
			}
		}
	}

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu); 
		menu.add(0, DELETE_WORK, 0,"Xóa").setIcon(android.R.drawable.ic_delete); 
		menu.add(0, ABOUT, 0,"Thông tin").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, SAVE, 0,"Lưu lại").setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, LOAD, 0,"Load lại từ hệ thống").setIcon(android.R.drawable.ic_menu_save);
		return true;
	} 
	//Xử lý sự kiện khi các option trong Option Menu được lựa chọn
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
			case DELETE_WORK: {
					deleteCheckedWork();
					break;
				} 
			case ABOUT: {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("THPT ANH HÙNG NÚP");
					builder.setMessage("AUTHOR: Duy Master" + "/n" + "Dự án hệ thống đóng ngắt tự động cho trường học /n" + "Facebook:/n" + "@viplazylmht/n" + "@paomat\n");
					builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() { 
							public void onClick(DialogInterface dialog, int which) {
							}
						});
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.show();
					break;
				}
			case SAVE: {
					try
					{
						save("/storage/emulated/0/AppProjects/Duy.txt");
					}
					catch (FileNotFoundException e)
					{ msg("Error while saving!");}
					break;
				} 
			case LOAD: {
					try
					{
						read("/storage/emulated/0/AppProjects/Duy.txt");
					}
					catch (FileNotFoundException e)
					{msg("Error while loading!");}
					break;
				} 
		}
		return true;
	}

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Đang kết nối...", "Vui lòng chờ !!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Kết nối thất bại, vui lòng thử lại!");
                finish();
            }
            else
            {
                msg("Đã kết nối.");
                isBtConnected = true;
				mConnectedThread = new ConnectedThread(btSocket);
				mConnectedThread.start();
            }
            progress.dismiss();
        }
    }
	private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
            	//Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];  
            int bytes; 

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget(); 
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {  
            	//if you cannot write, close the application
            	Toast.makeText(getBaseContext(), "Lỗi khi đọc ghi dữ liệu", Toast.LENGTH_LONG).show();
            	finish();

			}
		}
	}
}
