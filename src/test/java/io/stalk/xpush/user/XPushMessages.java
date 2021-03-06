package io.stalk.xpush.user;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import junit.framework.Assert;
import io.stalk.xpush.ChannelConnection;
import io.stalk.xpush.XPush;
import io.stalk.xpush.XPushData;
import io.stalk.xpush.XPushEmitter;
import io.stalk.xpush.exception.AuthorizationFailureException;
import io.stalk.xpush.exception.ChannelConnectionException;
import io.stalk.xpush.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.nkzawa.emitter.Emitter;

public class XPushMessages {
	//public static final String host = "http://stalk-front-s01.cloudapp.net:8000";
	public static final String host = XPushTestProperites.HOST;
	public static final String appId = XPushTestProperites.APP_ID;
	
	public static final String[] users_id = {"USER100","USER101","USER102","USER103","USER104"};
	public static final String[] devices_id = {"WEB","WEB","WEB","WEB","WEB"};
	public static final String password = "1q2w3e4r";
	
	public static final String wrongHost = "http://www.naver.com";
	public static final String doesNotExistAppId = "honggildong";

	public final String MSG_KEY = "XPush-Test-Data-Key";
	public JSONObject value;
	
	
	@Before
	public void setUp(){
		value = new JSONObject();
		try {
			value.put("data1", "value1");
			value.put("data2", "value2");
			value.put("data3", "value3");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// login 1 and login 2
	// user 1 send message three times in five seconds
	// login 2 first message is recieved then channel connection complete.
	@Test
	public void loginAndSendMessage() throws InterruptedException {
		final XPushMessages self = this;
		final XPush xpush = new XPush(host, appId);
		final XPush xpush2 = new XPush(host, appId);
		
		try {
			xpush.login(users_id[0], password, devices_id[0]);
			xpush2.login(users_id[1], password, devices_id[1]);
		} catch (AuthorizationFailureException e) {
			System.out.println("인증 오류"+e.getStatus()+"-"+e.getMessage());
		} catch (ChannelConnectionException e) {
			System.out.println("서버 연결 오류 : "+e.getStatus()+"-"+e.getMessage());
		} catch (IOException e) {
			System.out.println("서버 연결 오류 : "+e.getMessage());
		}
			
		xpush.createChannel(new String[]{users_id[1]}, null, null, new XPushEmitter.createChannelListener() {
			@Override
			public void call(ChannelConnectionException e, String channelName,
					ChannelConnection ch, List<User> users) {
				final String sendChNm = channelName;
				
				Assert.assertNotNull("Create channel but channelName is empty!!!", channelName);
				Assert.assertNotNull("Create channel but channelObject is empty!!!", channelName);
				
				System.out.println(channelName);
				
				xpush2.onMessageReceived(new XPushEmitter.messageReceived() {
					@Override
					public void call(String channelName, String key, JSONObject value) {
						Assert.assertEquals("this is not same channel!", channelName, sendChNm);
						Assert.assertEquals("this is not same key!", key, MSG_KEY);
						value.remove(XPushData.CHANNEL_ID);
						value.remove(XPushData.TIMESTAMP);
						Assert.assertEquals("this is not same value!", value.toString(), self.value.toString());
					}
				});
				
				xpush.send(channelName, MSG_KEY, value, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						System.out.println("************************* sendMessage");
					}
				});
			}
		});
			
		Thread.sleep(5000);
	}
	
	// login 1 and login 2
	// user 1 send message three times in five seconds
	// login 2 first message is recieved then channel connection complete.
	@Test
	public void confirmUnreadMessage() throws InterruptedException{
		final XPushMessages self = this;
		final XPush xpush = new XPush(host, appId);
		final XPush xpush2 = new XPush(host, appId);
		
		try {
			xpush.login(users_id[1], password, devices_id[1]);
			
			xpush.onMessageReceived(new XPushEmitter.messageReceived() {
				@Override
				public void call(String channelName, String key, JSONObject value) {
					System.out.println("################################ "+channelName+":"+key);
				}
			});
			
			xpush.createChannel(new String[]{users_id[0]}, null, null, new XPushEmitter.createChannelListener() {
				@Override
				public void call(ChannelConnectionException e, String channelName,
						ChannelConnection ch, List<User> users) {
					final String sendChNm = channelName;
					
					Assert.assertNotNull("Create channel but channelName is empty!!!", channelName);
					Assert.assertNotNull("Create channel but channelObject is empty!!!", channelName);
					
					System.out.println(channelName);

					
					xpush.send(channelName, MSG_KEY, value, new Emitter.Listener() {
						@Override
						public void call(Object... args) {
							System.out.println("************************* sendMessage");
							try {
								xpush2.login(users_id[2], password, devices_id[2]);
							} catch (AuthorizationFailureException e1) {
								e1.printStackTrace();
							} catch (ChannelConnectionException e1) {
								e1.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			});
			
		} catch (AuthorizationFailureException e) {
			System.out.println("인증 오류"+e.getStatus()+"-"+e.getMessage());
		} catch (ChannelConnectionException e) {
			System.out.println("서버 연결 오류 : "+e.getStatus()+"-"+e.getMessage());
		} catch (IOException e) {
			System.out.println("서버 연결 오류 : "+e.getMessage());
		}
		Thread.sleep(5000);
	}
}
