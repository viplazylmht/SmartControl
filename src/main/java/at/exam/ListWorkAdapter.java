package at.exam;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import 
android.widget.CompoundButton.OnCheckedChangeListener;
public class ListWorkAdapter extends 
ArrayAdapter<Work>{
 ArrayList<Work> array;
 int resource;
 Context context;
 public ListWorkAdapter(Context context, int 
textViewResourceId,
 ArrayList<Work> objects) {
 super(context, textViewResourceId, objects);
 this.context = context;
 resource = textViewResourceId;
 array = objects; 
 } 
//Phương thức xác định View mà Adapter hiển thị, ở đây chính là CustomViewGroup
 //Bắt buộc phải Override khi kế thừa từ ArrayAdapter
 @Override
 public View getView(int position, View convertView, 
ViewGroup parent) {
 View workView = convertView;
 
 if (workView == null) {
 workView = new 
CustomViewGroup(getContext());
 }
 
 ////Lấy về đối tượng Work hiện tại
 final Work work = array.get(position);
 if (work != null) {
 TextView workContent = ((CustomViewGroup) 
workView).workContent;
 TextView hourContent = ((CustomViewGroup)
workView).hourContent;
 TextView minContent = ((CustomViewGroup)
 workView).minContent;
 CheckBox checkWork = ((CustomViewGroup) 
workView).cb;
 
 //Set sự kiện khi đánh dấu vào checkbox trên list
 checkWork.setOnCheckedChangeListener(new 
OnCheckedChangeListener() {
 @Override
 public void 
onCheckedChanged(CompoundButton buttonView,
 boolean isChecked) {
 work.setChecked(isChecked); 
 } 
 });
 
//Lấy về nội dung cho TextView và CheckBox dựa vào đối tượng Work hiện tại
 workContent.setText(work.getContent());
 hourContent.setText(work.getHour());
 minContent.setText(work.getMin());
 checkWork.setChecked(work.isChecked());
 } 
 return workView;
 } 
}
