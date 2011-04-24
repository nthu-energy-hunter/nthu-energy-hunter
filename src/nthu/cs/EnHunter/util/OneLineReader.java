package nthu.cs.EnHunter.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class OneLineReader {
	public static Long getValue(File _f, boolean _convertToMillis) {
		String text = null;
		try {
			FileInputStream fs = new FileInputStream(_f);
			DataInputStream ds = new DataInputStream(fs);
			text = ds.readLine();
			ds.close();
			fs.close();
		}catch( Exception e) {
			e.printStackTrace();
		}
		
		Long value = null;
		
		if(text != null) {
			try {
				value = Long.parseLong(text);
			}catch(Exception e) {
				value =null;
			}
			if(_convertToMillis && value != null)
				value = value/1000;
		}
		return value;
	}
}
