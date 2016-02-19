package commons;

import http.ImgCompare.FOLDERS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;

public class Utils {
	private static final Logger LOGGER = Logger.getLogger(Utils.class);
	public static String getBase64encodedString(String fileName){
		try {
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			fis.read(imageData);
			fis.close();
			return Base64.encodeBase64String(imageData);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			LOGGER.debug("File with name:"+fileName+" is not found");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		return null;
	}
	
	public static void decodeBase64encodedString(String image,String pathToStore){
			
			try {
				File f = new File(pathToStore);
				f.getParentFile().mkdirs();
				FileOutputStream imageOutPutFile = new FileOutputStream(f);
				imageOutPutFile.write(Base64.decodeBase64(image));
				imageOutPutFile.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e);
			}
	}
	
	public static String getImagesNameForFolder(String page_FileName,Dimension dimension, FOLDERS folder){
		String ext;
		switch (folder) {
		case base:
			ext = "_base_";
			break;
		case newF:
			ext = "_new_";
			break;
		case diff:
			ext = "_diff_";
			break;
		default:
			throw new IllegalArgumentException("This library doenot except more folders to be created other than new, diff, base");
		}
		return page_FileName+ext+dimension.toString()+".png";
	}

}
