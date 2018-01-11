package at.exam;
public class Work {
	private String workContent;
	private int hourContent;
	private int minContent;
	private boolean isChecked;

	public Work(String workContent, int hourContent, int minContent) 
	{
		super();
		this.workContent = workContent;
		this.hourContent = hourContent;
		this.minContent = minContent;
		isChecked = false;
		
	}

	public String getContent() {
		return workContent;
	}
	public int getHour() {
		return hourContent;
	} 
	public int getMin() {
		return minContent;
	} 

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}
}

