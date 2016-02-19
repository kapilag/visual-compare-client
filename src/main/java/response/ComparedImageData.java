package response;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComparedImageData {
	
	private boolean isSameDimensions;
	private String misMatchPercentage;
	private Dimension dimensionDifference ;
	private int analysisTime;
	private String imageDataUrl;
	public boolean getIsSameDimensions() {
		return isSameDimensions;
	}
	public void setIsSameDimensions(boolean isSameDimensions) {
		this.isSameDimensions = isSameDimensions;
	}
	public String getMisMatchPercentage() {
		return misMatchPercentage;
	}
	public void setMisMatchPercentage(String misMatchPercentage) {
		this.misMatchPercentage = misMatchPercentage;
	}
	public String getImageDataUrl() {
		return imageDataUrl;
	}
	public void setImageDataUrl(String imageDataUrl) {
		this.imageDataUrl = imageDataUrl;
	}
	public Dimension getDimensionDifference() {
		return dimensionDifference;
	}
	public void setDimensionDifference(Dimension dimensionDifference) {
		this.dimensionDifference = dimensionDifference;
	}
	public int getAnalysisTime() {
		return analysisTime;
	}
	public void setAnalysisTime(int analysisTime) {
		this.analysisTime = analysisTime;
	}
	
	public String toString(){
		return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
	

}
