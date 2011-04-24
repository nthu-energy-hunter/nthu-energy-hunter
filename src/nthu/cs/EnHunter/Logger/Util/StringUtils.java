package nthu.cs.EnHunter.Logger.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {
	public static String inputStreamToString(InputStream stream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
