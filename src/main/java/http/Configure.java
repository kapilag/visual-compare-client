package http;

import java.io.File;

public class Configure {

	private String host;
	private int port;
	private float mismatchPerc;
	String root = System.getProperty("user.dir");
	private  String baselineImageFolder = root+"/"+"base/".replace("/", File.separator);
	private  String newImageFolder = root+"/"+"new/".replace("/", File.separator);
	private  String diffImageFolder = root+"/"+"diff/".replace("/", File.separator);
	private String apiEndPoint;
	private boolean updateBaseLine = false;
	
	public Configure(String host,int port,int mismatchPercentage){
		this.host = host;
		this.port = port;
		this.mismatchPerc = mismatchPercentage;
		apiEndPoint = "http://"+host+":"+port+"/compare";
	}
	
	
	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
	
	public float getMismatchPerc(){
		return mismatchPerc;
	}

	public String getBaselineImageFolder() {
		return baselineImageFolder;
	}

	public void setBaselineImageFolder(String baselineImageFolder) {
		this.baselineImageFolder = baselineImageFolder;
	}

	public String getNewImageFolder() {
		return newImageFolder;
	}

	public void setNewImageFolder(String newImageFolder) {
		this.newImageFolder = newImageFolder;
	}

	public String getDiffImageFolder() {
		return diffImageFolder;
	}

	public void setDiffImageFolder(String diffImageFolder) {
		this.diffImageFolder = diffImageFolder;
	}
	
	public String getApiEndPoint(){
		return apiEndPoint;
	}


	public boolean isUpdateBaseLine() {
		return updateBaseLine;
	}


	public void setUpdateBaseLine(boolean updateBaseLine) {
		this.updateBaseLine = updateBaseLine;
	}
	
	
	
}
