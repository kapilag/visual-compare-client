package request;

public class Images {
	
	private String beforeT;
	private String afterT;
	public String getBefore() {
		return beforeT;
	}
	public void setBefore(String before) {
		this.beforeT = before;
	}
	public String getAfter() {
		return afterT;
	}
	public void setAfter(String after) {
		this.afterT = after;
	}
	
	@Override
	public String toString(){
		return "[after:"+afterT+",before:"+beforeT+"]";
	}

}
