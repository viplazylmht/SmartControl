package ahn.research.viplazy;
public class Work {
 private String workContent;
 private String timehContent;
 private String timemContent;
 private String timeDelay;
 private boolean isChecked;
 
 public Work(String workContent, String timehContent,String timemContent, String timeDelay) 
{
 this.workContent = workContent;
 this.timehContent = timehContent;
 this.timemContent = timemContent;
 this.timeDelay = timeDelay;
 isChecked = false;
 }
 
 public String getContent() {
 return workContent;
 }
 public String getDelay() {
	 return timeDelay;
 }
 public String getTimeh() {
 return timehContent;
 }
public String getTimem() {
	return timemContent;
}
 public void setChecked(boolean isChecked) {
 this.isChecked = isChecked;
 }
 
 public boolean isChecked() {
 return isChecked;
 }
}
