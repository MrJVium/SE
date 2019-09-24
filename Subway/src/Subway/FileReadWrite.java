package Subway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileReadWrite {
	
	public static String readJsonData(String pactFile){
		StringBuffer strbuffer = new StringBuffer();
		File myFile = new File(pactFile);
		if (!myFile.exists()) {
			System.err.println("Can't Find " + pactFile);
		}
		try {
			FileInputStream fis = new FileInputStream(pactFile);
			InputStreamReader inputStreamReader = new InputStreamReader(fis, "GBK");
			BufferedReader in  = new BufferedReader(inputStreamReader);
			
			String str;
			while ((str = in.readLine()) != null) {
				strbuffer.append(str);  //new String(str,"UTF-8")
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return strbuffer.toString();
	}
	
	public static boolean write2File(String filePath, String sets) {
        FileWriter fw;
        try {
        	File f=new File(filePath);
        	FileOutputStream fos1=new FileOutputStream(f);
        	OutputStreamWriter dos1=new OutputStreamWriter(fos1);
        	dos1.write(sets);
        	dos1.write("\n");
        	dos1.close();
        	return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public static String getPath(String fileName) {
		String path = Subway.class.getResource("").toString() + fileName;
		path = path.replace("file:/", "");
		path = path.replace("/", "//");
		return path;
	}
}
