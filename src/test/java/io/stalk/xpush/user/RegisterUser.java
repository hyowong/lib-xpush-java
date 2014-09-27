package io.stalk.xpush.user;

import io.stalk.xpush.XPush;
import io.stalk.xpush.exception.AuthorizationFailureException;
import io.stalk.xpush.exception.ChannelConnectionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.JsonObject;


public class RegisterUser {
	
	private String host = "http://stalk-front-s01.cloudapp.net:8000";
	private String appId = "test-app";
	
	private String[] users_id = {"USER100","USER101","USER102"};
	private String[] devices_id = {"WEB","WEB","WEB"};
	private String password = "1q2w3e4r";
	
	private String wrongHost = "http://www.naver.com";
	private String doesNotExistAppId = "honggildong";

	@Test
	public void signupAndLogin(){
		XPush xpush = new XPush(host, appId);
		try {
			xpush.signup(users_id[0], password, devices_id[0],"NOTIID");
			xpush.signup(users_id[1], password, devices_id[1],"NOTIID");
			xpush.signup(users_id[2], password, devices_id[2],"NOTIID");
			System.out.println("register user success");
		} catch (AuthorizationFailureException e) {
			e.printStackTrace();
			if(AuthorizationFailureException.USER_EXIST.equals(e.getMessage())){
				System.out.println("사용자가 존재합니다.");
			}
		} catch (ChannelConnectionException e) {
			e.printStackTrace();
		}
		System.out.println("사용자 등록 완료");
				
		try {
			xpush.login(users_id[0], password, devices_id[0]);
			xpush.login(users_id[1], password, devices_id[1]);
			xpush.login(users_id[2], password, devices_id[2]);
		} catch (AuthorizationFailureException e) {
			e.printStackTrace();
		} catch (ChannelConnectionException e) {
			e.printStackTrace();
		}
		System.out.println("로그인 완료");
	}
	
    @Test(expected=ChannelConnectionException.class)                                     
    public void confirmAddress() throws Exception{
    	XPush xpush = new XPush(wrongHost, appId);
    	String returnLogin = null;
		returnLogin = xpush.login(users_id[0], password, devices_id[0]);
		
    	XPush xpush2 = new XPush(host, appId);
    	String returnLogin2 = null;
			returnLogin2 = xpush2.login(users_id[0], password, devices_id[0]);
		
    	System.out.println(returnLogin);
    	Assert.assertEquals(null, returnLogin);   
    	Thread.sleep(5000);
    }
    
    @Test(expected=AuthorizationFailureException.class)                                     
    public void confirmApplicationId() throws Exception{
    	XPush xpush = new XPush(host, doesNotExistAppId);
    	String returnLogin = null;
		returnLogin = xpush.login(users_id[0], password, devices_id[0]);
		
    	XPush xpush2 = new XPush(host, appId);
    	String returnLogin2 = null;
			returnLogin2 = xpush2.login(users_id[0], password, devices_id[0]);
		
    	System.out.println(returnLogin);
    	Assert.assertEquals(null, returnLogin);   
    	Thread.sleep(5000);
    }
    
	/*

    @Test
    public void sendMessage() throws InterruptedException{
    	System.out.println("==== sendMessage");
    	XPush xpush = new XPush(host, appId);
    	
    	String returnLogin = xpush.login("notdol102", "win1234", "WEB");
    	System.out.println(returnLogin);
    	Assert.assertEquals(null, returnLogin);   
    	
    	JSONObject sendObject = new JSONObject();
    	try {
			sendObject.put("key", "value");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	xpush.send("TEST_CH01", "TESTKEY", sendObject, new Emitter.Listener() {
			
			public void call(Object... arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	Assert.assertEquals(null, returnLogin);   
    	Thread.sleep(5000);
    }
    */
    
    
}
