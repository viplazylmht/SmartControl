package at.exam;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.webkit.*;
import android.content.*;
import android.net.*;
import java.util.*;
import android.app.*;
import android.widget.*;
public class MainActivity extends Activity { 
 //Các hằng dùng cho tạo Option Menu
 private static final int DELETE_WORK = Menu.FIRST;
 private static final int ABOUT = Menu.FIRST + 2;
  int gio=0;
  int phut=0;
 ArrayList<Work> array;
 ListWorkAdapter arrayAdapter;
 @Override
 public void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.main);
 
 array = new ArrayList<Work>();
 arrayAdapter = new ListWorkAdapter(this,
 R.layout.list, array);
 
 final EditText workEnter = (EditText) 
findViewById(R.id.work_enter);
 final TextView time = (TextView) findViewById(R.id.txtime);
// final EditText hourEdit = (EditText) findViewById(R.id.hour_edit);
// final EditText minuteEdit = (EditText) findViewById(R.id.minute_edit);
 
 final Button button = (Button) 
findViewById(R.id.button);
final Button button_set = (Button) 
findViewById(R.id.time);
 
 //Tạo list view cho danh sách công việc
	final ListView list = (ListView) findViewById(R.id.list);
	list.setAdapter(arrayAdapter);

    // perform click event listener on edit text
	button_set.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Calendar mcurrentTime = Calendar.getInstance();
			int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
			int minute = mcurrentTime.get(Calendar.MINUTE);
			TimePickerDialog mTimePicker;
			mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
					//    time.setText(selectedHour + ":" + selectedMinute);
					gio = selectedHour;
					phut = selectedMinute;
					time.setText(gio + " giờ " + phut + " phút.");
				}
			}, hour, minute, true);            // Dung dinh dang 24h
			mTimePicker.setTitle("Chọn Thời Gian");
			mTimePicker.show();
		}
	});

	OnClickListener add = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (workEnter.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Rất tiếc");
				builder.setMessage("Vui lòng kiểm tra lại thông tin");
				builder.setPositiveButton("Tiếp Tục", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub 
					} 
				});
				builder.show();
			}
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Cong doan tiep theo");
				builder.setMessage("Nhap vao thoi gian bat dau:");
				builder.setPositiveButton("Tiếp Tục", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub 
							Calendar mcurrentTime = Calendar.getInstance();
							int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
							int minute = mcurrentTime.get(Calendar.MINUTE);
							TimePickerDialog mTimePicker;
							mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
									@Override
									public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
										//    time.setText(selectedHour + ":" + selectedMinute);
										gio = selectedHour;
										phut = selectedMinute;
										time.setText(gio + " giờ " + phut + " phút.");
										String workContent = workEnter.getText().toString();
										System.out.println("DEBUG: DUY: " + gio + " giờ " + phut);
										Work work = new Work(workContent, gio , phut);
										array.add(0, work);
										arrayAdapter.notifyDataSetChanged();
										workEnter.setText("");
									}
								}, hour, minute, true);            // Dung dinh dang 24h
							mTimePicker.setTitle("Chọn Thời Gian");
							mTimePicker.show();
						} 
					});
				builder.show();
			}
		}
	};
	button.setOnClickListener(add); 
}
 
 //Tạo Option Menu
 public boolean onCreateOptionsMenu(Menu menu) {
 super.onCreateOptionsMenu(menu); 
 menu.add(0, DELETE_WORK, 0,"Xóa các lựa chọn" 
).setIcon(android.R.drawable.ic_delete); 
 menu.add(0, ABOUT, 0,"Thông tin" 
).setIcon(android.R.drawable.ic_menu_info_details);
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
 AlertDialog.Builder builder = new 
AlertDialog.Builder(MainActivity.this);
 builder.setTitle("THPT Anh Hùng Núp");
 builder.setMessage("Chủ:" + " Hà Văn Duy" + "\n" + "Dự án:" + " Đèn điện và mạch tự động" + "\n" + "Facebook: @viplazylmht");
 builder.setPositiveButton("Leave My Page", new 
DialogInterface.OnClickListener() { 
 public void onClick(DialogInterface 
dialog, int which) {
	Intent i = new Intent(Intent.ACTION_VIEW,
	  Uri.parse("http://www.facebook.com/viplazylmht"));
	  startActivity(i);
	}
 });
 
builder.setIcon(android.R.drawable.ic_dialog_info);
 builder.show();
 break;
 }
 }
 return true;
 }
 private void deleteCheckedWork() {
 	if (array.size() > 0) {
 		for (int i = 0; i < array.size(); i++) {
 			if (i > array.size()) { break;}
 			if (array.get(i).isChecked()) {
			 	array.remove(i);
				 arrayAdapter.notifyDataSetChanged();
				 // continue;
			 }
 		}
	 }
 }
}
