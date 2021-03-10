package jforgame.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * http工具类
 * @author kinson
 *
 */
public class HttpUtil {


	public static String buildUrlParam(Object... params) {
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("参数个数必须为偶数");
		}
		StringBuffer result = new StringBuffer("");
		try {
			for (int i=0; i<params.length; i+=2) {
				if (result.length() > 0) {
					result.append("&");
				}
				result.append(params[i]);
				String value = URLEncoder.encode(params[i+1].toString(), "UTF-8");
				result.append("=" + value);
			}
		}catch(Exception e) {
		}
		return result.toString();
	}

	public static String get(String urlAddr) throws IOException {
		HttpURLConnection uc = null;
		try {
			URL url = new URL(urlAddr);
			uc = (HttpURLConnection)url.openConnection();
			uc.setDoInput(true);
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(5000);

			uc.connect();

			StringBuffer result = new StringBuffer("");
			if (uc.getResponseCode() == 200) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
					while (reader.ready()) {
						result.append(reader.readLine());
					}
				}
			}
			return result.toString();
		} catch(IOException e1) {
			throw e1;
		} catch(Exception e) {
			throw new IOException(e);
		}finally {
			if (uc != null && uc.getInputStream() != null) {
				uc.getInputStream().close();
			}
		}
	}

	public static String post(String urlAddr) throws Exception {
		HttpURLConnection uc = null;
		try {
			URL url = new URL(urlAddr);
			uc = (HttpURLConnection)url.openConnection();
			uc.setDoInput(true);
			uc.setDoOutput(true);
			uc.setRequestMethod("POST");
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(5000);

			uc.connect();

			StringBuffer result = new StringBuffer("");
			if (uc.getResponseCode() == 200) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
					while (reader.ready()) {
						result.append(reader.readLine());
					}
				}
			}
			return result.toString();
		}catch(Exception e) {
			return "";
		}finally {
			if (uc != null && uc.getInputStream() != null) {
				uc.getInputStream().close();
			}
		}
	}


	public static void main(String[] args) throws Exception {
		String param = buildUrlParam("a",3,"b",4);
		String url = "http://www.baidu.com" + "?" + param;
		System.err.println(url);
		System.err.println(get(url));
	}

}
