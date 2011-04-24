package nthu.cs.EnHunter.Logger.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import nthu.cs.EnHunter.Logger.Uploder.Global;
import nthu.cs.EnHunter.Logger.Util.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HTTPRequestHelper {
	
	private static final int TIMEOUT = 300*1000;
	
	private static final String CONTENT_TYPE = "Content-Type";
	//private static final String MIME_FORM_ENCODE = "application/x-www-form-urlencoded";
	private static final String MIME_FORM_ENCODE = "application/json";
	private static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String RESPONSE_SUCCESS = "RESPONSE_SUCCESS";
	public static final String RESPONSE = "RESPONSE";
	private static final int POST_TYPE = 1;
	
	private static final String TAG = "EnHunter - Logger - HTTPReqHelper";
	
	private final ResponseHandler<String> responseHandler;
	
	public HTTPRequestHelper(final ResponseHandler<String> resHandler){
		
		
		
		this.responseHandler = resHandler;
		
	}
	/**
	 * Perform an HTTP POST Request
	 * */
	public void doPost(final String url, final String usr, 
			final Map<String, String> additionalHeaders, final Map<String, String> params) {
		performRequest(HTTPRequestHelper.MIME_FORM_ENCODE, url, usr, additionalHeaders, params, HTTPRequestHelper.POST_TYPE);
	}
	
	private void performRequest(final String contentType,final String url,final String usr, 
			final Map<String, String> headers,final Map<String, String> params, int Type) {
		if(Global.DEBUG)
			Log.d(TAG,"performRequest to "+ url);
		
		// establish HTTPClient
		HttpParams hp = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(hp, TIMEOUT);
		
		DefaultHttpClient client = new DefaultHttpClient(hp);
		
		final Map<String, String> sendHeaders = new HashMap<String, String>();
		if ((headers != null) && (headers.size()) > 0) {
			sendHeaders.putAll(headers);
		}
		if (Type == HTTPRequestHelper.POST_TYPE) {
			sendHeaders.put(HTTPRequestHelper.CONTENT_TYPE, contentType);
		}
		
		if( Type == HTTPRequestHelper.POST_TYPE) {
			HttpPost method = new HttpPost(url);
			
			long transferLength = 0;
			
			List<NameValuePair> nvps = null;
			
			if ((params != null) && (params.size() > 0)) {
				nvps = new ArrayList<NameValuePair>();
				for ( String key: params.keySet()) {
					
					if(Global.DEBUG)
						Log.d(TAG,"ket = "+key + " val = "+params.get(key) );
					nvps.add(new BasicNameValuePair(key, params.get(key)));
					transferLength += params.get(key).length();
					
				}
			}
			if (nvps != null) {
				try {
					method.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					if(Global.DEBUG)
						Log.d(TAG,"type:"+method.getEntity().getContentType() );
				
					try {
						Log.d(TAG,"content:"+StringUtils.inputStreamToString(method.getEntity().getContent()));
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch(UnsupportedEncodingException e) {
					
				}
			}
			execute(client, method);
		}
		
		
		
		
		
	}

	/**
	 * Execute the request
	 * @param client
	 * @param method
	 * */
	private void execute(DefaultHttpClient client, HttpPost method) {
		// TODO Auto-generated method stub
		BasicHttpResponse errorResponse = new BasicHttpResponse(new ProtocolVersion("HTTP_ERROR",1,1), 500, "ERROR");
		
		try {

			client.execute(method, this.responseHandler);
			
		} catch(Exception e) {
			errorResponse.setReasonPhrase(e.getMessage());
			try {
				this.responseHandler.handleResponse(errorResponse);
			}catch(Exception ex) {
				if ( Global.DEBUG)
					Log.d(TAG, ex.toString());
			}
		}
	}
	
	/**
	 * Static utility method to create a default ResponseHandler that send a Message to the passed 
	 * in Handler with response as a String , after the HTTP request is completed.
	 * 回傳一個 ResponseHandler , 其作用是
	 * 當HTTPResponseHandler收到Request Response時, 發送一個String訊息(Message)給傳入的Handler,請他做對應的處理 
	 */
	public static ResponseHandler<String> getResponseHandlerInstance(final Handler handler) {
		
		
		final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				Message message = handler.obtainMessage(); // init a message
				Bundle bundle = new Bundle();
				StatusLine status = response.getStatusLine();
				
				HttpEntity entity = response.getEntity();
				String result = null;
				if (entity != null) {
					try {
						result = StringUtils.inputStreamToString(entity.getContent());
						bundle.putBoolean(RESPONSE_SUCCESS,true);
						bundle.putString(RESPONSE, result);
						message.setData(bundle);
						handler.sendMessage(message);
						
					}catch (Exception e) {
						
					}
				}else {
					bundle.putBoolean(RESPONSE_SUCCESS,false);
					bundle.putString(RESPONSE, "Error - "+ response.getStatusLine().getReasonPhrase());
					message.setData(bundle);
					handler.sendMessage(message);
				}
				
				return result;
			}
			
		};
		return responseHandler;
	}
}
