package com.sccp.library.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sccp.library.util.StringUtil;

import android.util.Log;

public class BaseHttpClient {

	private static BaseHttpClient mInstance = null;
	private final static String LOG_TAG = "BaseHttpClient:";
	private final static String BOUNDARY =  UUID.randomUUID().toString(); 
	private final static String PREFIX = "--";
	private final static String LINE_END = "\r\n";
	private final static String CONTENT_TYPE = "multipart/form-data";
	private HttpClient client = null;
	private	HttpParams params = null;

	public static BaseHttpClient getInstance() {
		
		if (mInstance == null) {
			mInstance = new BaseHttpClient();
		}
		return mInstance;
	}
	
	public BaseHttpClient() {
		init();
	}
	
	private void init() {

		params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpProtocolParams.setUserAgent(params,
			"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
			+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
								
		// 超时设置
		// 从连接池中取连接的超时时间
		ConnManagerParams.setTimeout(params, 2000);	
		// 连接超时
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		// 请求超时
		HttpConnectionParams.setSoTimeout(params, 5000);

		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		client = new DefaultHttpClient(conMgr, params);
		
		mInstance = this;
	}

	public BaseHttpClient(int readTimeOutMillis) {
		
		params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpProtocolParams.setUserAgent(params,
			"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
			+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
								
		// 超时设置
		// 从连接池中取连接的超时时间
		ConnManagerParams.setTimeout(params, 2000);	
		// 连接超时
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		// 请求超时
		HttpConnectionParams.setSoTimeout(params, readTimeOutMillis);

		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		client = new DefaultHttpClient(conMgr, params);
	}
	
	public String getRequest(String url){
		
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse httpResponse = client.execute(get);

			Log.d(LOG_TAG + "getRequest", "URL:" + url + " HTTP_CODE:" + httpResponse.getStatusLine().getStatusCode());
			
			if(httpResponse.getStatusLine().getStatusCode() == 200) {
				
				String result = null;
				HttpEntity httpEntity = httpResponse.getEntity();
				
				if(httpEntity == null) {
					return null;
				}
				
				byte[] bytes = EntityUtils.toByteArray(httpEntity);
				
				if(StringUtil.isEmpty(EntityUtils.getContentCharSet(httpEntity))) {
					result = new String(bytes, HTTP.UTF_8);
				}
				else{
					result = new String(bytes, EntityUtils.getContentCharSet(httpEntity));
				}
				
				return result.trim();
				
			}
			else{
				return String.valueOf(httpResponse.getStatusLine().getStatusCode());
			}
			
		}
		catch(ConnectException e) {
			
			e.printStackTrace();
			return "ConnectTimeout";
		}
		catch(SocketTimeoutException e) {
			
			e.printStackTrace();
			return "SocketTimeout";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String postRequest(String url,Map<String, String> map){
		
		try {
			//传递个数多 可对参数封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			for(String key : map.keySet()) {
				
				Log.d(LOG_TAG + "postRequest-params-key-value", key + ":" + map.get(key));
				params.add(new BasicNameValuePair(key, map.get(key)));
			}

			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
			HttpResponse httpResponse = client.execute(post);
			
			Log.d(LOG_TAG + "postRequest", "URL:" + url + " HTTP_CODE:" + httpResponse.getStatusLine().getStatusCode());
			
			if(httpResponse.getStatusLine().getStatusCode() == 200) {
				
				String result = null;
				HttpEntity httpEntity = httpResponse.getEntity();
				
				if(httpEntity == null) {
					return null;
				}
				
				byte[] bytes = EntityUtils.toByteArray(httpEntity);
				
				if(StringUtil.isEmpty(EntityUtils.getContentCharSet(httpEntity))) {
					result = new String(bytes, HTTP.UTF_8);
				}
				else{
					result = new String(bytes, EntityUtils.getContentCharSet(httpEntity));
				}
				
				return result.trim();
			}
			else {
				return String.valueOf(httpResponse.getStatusLine().getStatusCode());
			}
			
		}
		catch(ConnectException e) {
			
			e.printStackTrace();
			return "ConnectTimeout";
		}
		catch(SocketTimeoutException e) {
			
			e.printStackTrace();
			return "SocketTimeout";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public InputStream getInputStream(String url) {
		
		InputStream is = null;
		
		try {
			
			HttpGet get = new HttpGet(url); 
			HttpResponse response = client.execute(get);
			
			Log.d(LOG_TAG + "getInputStream", "URL:" + url + " HTTP_CODE:" + response.getStatusLine().getStatusCode());
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity httpEntity = response.getEntity();
				
				if(httpEntity == null) {
					return null;
				}
				
				is = httpEntity.getContent();
				return is;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			closeInputStream(is);
		}
		
		return null;
	}
	
    /**
     * close inputStream
     * 
     * @param is
     */
    private static void closeInputStream(InputStream is) {
    	
        if (is == null) {
            return;
        }

        try {
            is.close();
        }
        catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        }
    }
	
	public InputStream getImageInputStream(String url) {
		
		BaseHttpClient httpClient = new BaseHttpClient(10000);
		return httpClient.getInputStream(url);
	}

	public String uploadImage(File file,String keyname,String url,Map<String, String> map) {
		
		try {
			
			URL Url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";
			
			if (map != null && map.size() > 0) {
				
				Iterator<String> it = map.keySet().iterator();
				
				while (it.hasNext()) {
					
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = map.get(key);
					
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
					sb.append(value).append(LINE_END);
					
					params = sb.toString();
					Log.d("HttpUrlConnection", key+"="+params+"##");
					dos.write(params.getBytes());
				}
			}
			
			sb = null;
			params = null;
			sb = new StringBuffer();
		
			sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + keyname
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/pjpeg" + LINE_END); 
			sb.append(LINE_END);
			
			params = sb.toString();
			sb = null;
			
			Log.d(LOG_TAG + "uploadImage", "Filename=" + file.getName() + " Body=" + params+"##");
			dos.write(params.getBytes());
			InputStream is = new FileInputStream(file);
			
			byte[] bytes = new byte[1024];
			int len = 0;
			
			while ((len = is.read(bytes)) != -1) {
				dos.write(bytes, 0, len);
			}
			
			is.close();
			
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			
			if(conn.getResponseCode() == 200) {
				
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				
				return sb1.toString();
			}
			else{
				return String.valueOf(conn.getResponseCode());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}