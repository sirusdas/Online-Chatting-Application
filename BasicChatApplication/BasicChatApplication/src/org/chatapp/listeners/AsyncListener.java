
package org.chatapp.listeners;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncEvent;

import org.chatapp.useroperation.Client;



public class AsyncListener implements javax.servlet.AsyncListener {

	private final String name;
	private final Map<String, List<Client>> model;
	private final String channel;
	private final Client client;

	public AsyncListener(String name, Map<String, List<Client>> model, String channel, Client subscriber){
		this.name = name;
		this.model = model;
		this.channel = channel;
		this.client = subscriber;
	}
	
	
	public void onComplete(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onComplete for " + client.getUserName());
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	
	public void onTimeout(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onTimeout for " + client.getUserName());
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	
	public void onError(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onError for " + client.getUserName());
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	
	public void onStartAsync(AsyncEvent event) throws IOException {
		System.out.println("onStartAsync for " + client.getUserName());
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	private void removeFromModel() {
		System.out.println("Removed Client"+client.getUserName());
		model.get(channel).remove(client);
	}
	
}
