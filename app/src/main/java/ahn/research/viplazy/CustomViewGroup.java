package ahn.research.viplazy;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
public class CustomViewGroup extends LinearLayout {
	public CheckBox cb;
	public TextView workContent;
	public TextView timehContent;
	public TextView timemContent;
	public TextView timeDelay;
	public CustomViewGroup(Context context) {
		super(context);
		//Sử dụng LayoutInflater để gán giao diện trong list.xml cho class này 
		LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.list, this, true);
		//Lấy về các View qua Id
		cb = (CheckBox) findViewById(R.id.check_work);
		workContent = (TextView) findViewById(R.id.work_content);
		timehContent = (TextView) findViewById(R.id.timeh_content);
		timemContent = (TextView) findViewById(R.id.timem_content);
		timeDelay = (TextView) findViewById(R.id.delay_content);
	}
}
