package io.stalk.xpush.user;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.github.nkzawa.emitter.Emitter;

import io.stalk.xpush.ChannelConnection;
import io.stalk.xpush.XPush;
import io.stalk.xpush.XPushData;
import io.stalk.xpush.XPushEmitter;
import io.stalk.xpush.exception.AuthorizationFailureException;
import io.stalk.xpush.exception.ChannelConnectionException;
import io.stalk.xpush.model.User;

public class UserList {
	private String host = XPushTestProperites.HOST;
	private String appId = XPushTestProperites.APP_ID;
	
	private String[] users_id = {"USER100","USER101","USER102"};
	private String[] devices_id = {"WEB","WEB","WEB"};
	private String password = "1q2w3e4r";

	
	@Test
	public void getUserAllList(){
		final XPush xpush = new XPush(host, appId);
    	String returnLogin = null;
		try {
			returnLogin = xpush.login(users_id[0], password, devices_id[0]);
			returnLogin = xpush.login(users_id[0], password, devices_id[1]);
			returnLogin = xpush.login(users_id[0], password, devices_id[2]);
		} catch (AuthorizationFailureException e) {
			e.printStackTrace();
		} catch (ChannelConnectionException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(returnLogin);
		
		xpush.getUserList(new JSONObject(), new XPushEmitter.receiveUserList() {
			
			public void call(String err, List<User> users) {
				// TODO Auto-generated method stub
				System.out.println("*********** received user list");
				System.out.println("error : "+err);
				Assert.assertEquals("there is no users", true, users.size() > 0 );
				System.out.println("There are "+users.size()+" users exist!");
				xpush.disconnect();
			}
		});
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getUserListInChannel(){
		final XPush xpush = new XPush(host, appId);
    	String returnLogin = null;
		try {
			returnLogin = xpush.login(users_id[0], password, devices_id[0]);
		} catch (AuthorizationFailureException e) {
			System.out.println("인증 실패");
		} catch (ChannelConnectionException e){
			System.out.println("연결 실패");
		} catch (IOException e) {
			System.out.println("연결 실패");
		}
		System.out.println(returnLogin);

		xpush.createChannel(users_id, null, new JSONObject(), new XPushEmitter.createChannelListener() {
			public void call(ChannelConnectionException e, String channelName,
					ChannelConnection ch, List<User> users) {
				System.out.println("********* create Channel ");
				System.out.println(channelName);
				// TODO Auto-generated method stub
				xpush.getUserListInChannel(channelName, new XPushEmitter.receiveUserList() {
					
					public void call(String err, List<User> users) {
						// TODO Auto-generated method stub
						System.out.println(users);
						Assert.assertEquals("Users are not match!", users.size(),  3);
						xpush.disconnect();
					}
				});
				
			}
		});
		
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
