
package org.chatapp.useroperation;

import javax.servlet.AsyncContext;

public class Client {

	private String userName;
	private String channel;
	private final AsyncContext aCtx;
	
	public Client(AsyncContext aCtx, String channel) {
		this.aCtx = aCtx;
		this.channel = channel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getChannel() {
		return channel;
	}
	
	public AsyncContext getaCtx() {
		return aCtx;
	}

	@Override
	public String toString() {
		return "Client [userName=" + userName + "]";
	}
	
}
