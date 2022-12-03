package stand.ronghui;

import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.List;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.HashMap;

public class HttpRequest {
	public String cookies = "";
	public String text = "";

	public static HttpRequest get(String url, String[][] params, String[][] headers) {
		return get(buildParams(url, params), headers);
	}

	public static HttpRequest get(String url, String[][] headers) {
		HttpRequest request = new HttpRequest();
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			//拼接Header请求信息
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			buildHeader(connection, headers);
			connection.connect();
			//获取cookie
			request.cookies = getCookies(connection);
			//获取网页内容
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					result += line + "\n";
				}
				request.text = result;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return request;
	}

	public static HttpRequest post(String url, String data, String[][] headers) {
		HttpRequest request = new HttpRequest();
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			buildHeader(conn, headers);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			out.print(data);
			out.flush();
			request.cookies = getCookies(conn);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
				request.text = result;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return request;
	}

	private static void buildHeader(HttpURLConnection connection, String[][] headers) {
		for (int i = 0;i < headers.length;i++) {
			for (int j = 0;j < headers[i].length;j += 2) {
				connection.setRequestProperty(headers[i][j], headers[i][j + 1]);
			}
		}
	}

	private static String buildParams(String url, String[][] parms) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
		for (int i = 0;i < parms.length;i++) {
			for (int j = 0;j < parms[i].length;j += 2) {
				sb.append(parms[i][j] + "=" + parms[i][j + 1]);
			}
			if (i != parms.length) {
				sb.append("&");
			}
		}
		return sb.toString();
	}

	private static String getCookies(HttpURLConnection connection) {
		Map<String, List<String>> map = connection.getHeaderFields();
		List<String> cookiesList=map.get("Set-Cookie");
		String newcookie="";
		for (String cook : cookiesList) {
			newcookie += cook.split(";")[0] + "; ";
		}
		if (newcookie.length() != 0) {
			return newcookie;
		}
		return "";
	}
}
