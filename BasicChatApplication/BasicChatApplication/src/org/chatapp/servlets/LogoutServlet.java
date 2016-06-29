/*  
 * Copyright (c) 2011 Ant Kutschera
 * 
 * This file is part of Ant Kutschera's blog, 
 * http://blog.maxant.co.uk
 * 
 * This is free software: you can redistribute
 * it and/or modify it under the terms of the
 * Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * It is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the Lesser GNU General Public License for
 * more details. 
 * 
 * You should have received a copy of the
 * Lesser GNU General Public License along with this software.
 * If not, see http://www.gnu.org/licenses/.
 */
package org.chatapp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chatapp.listeners.AsyncListener;
import org.chatapp.useroperation.Client;

@WebServlet(name = "logoutServlet", urlPatterns = { "/logout" }, asyncSupported = true)
public class LogoutServlet extends HttpServlet {

	public static final String CLIENTS = "ClientMap";

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// dont set the content length in the response, and we will end up with chunked 
		// encoding so that a) we can keep the connection open to the client, and b) send
		// updates to the client as chunks.
		
		// *********************
		// we use asyncSupported=true on the annotation for two reasons. first of all, 
		// it means the connection to the client isn't closed by the container.  second 
		// it means that we can pass the asyncContext to another thread (eg the publisher) 
		// which can then send data back to that open connection.
		// so that we dont require a thread per client, we also use NIO, configured in the 
		// connector of our app server (eg tomcat)
		// *********************

		// what channel does the user want to subscribe to?  
		// for production we would need to check authorisations here!
		String channel = request.getParameter("channel");
		String userName = request.getParameter("userName");
		
		
		// get the application scope so that we can add our data to the model
		ServletContext appScope = request.getServletContext();

		// fetch the model from the app scope
		@SuppressWarnings("unchecked")
		Map<String, List<Client>> channelsClientsMap = (Map<String, List<Client>>) appScope.getAttribute(CLIENTS);
		
		List<Client> subscribers = channelsClientsMap.get(channel);
		synchronized (subscribers) {
			if(subscribers !=null ){
				for(Client s : subscribers){
					if(s.getUserName().equals(userName)){
						
							subscribers.remove(s);
					}
		
				}
			}
		}
		
		tellOtherAboutLogoutUser(channelsClientsMap,channel,userName);
		// acknowledge the subscription
		//aCtx.getResponse().getOutputStream().print("hello - you are subscribed to " + channel);
		//aCtx.getResponse().flushBuffer(); //to ensure the client gets this ack NOW
		/*
		try {
			request.getRequestDispatcher("/jsp/ChatWindow.jsp").forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void tellOtherAboutLogoutUser(Map<String, List<Client>> channelsClientsMap,String channel,String userName ){
		
		
		//List<Client> clients = channelsClientsMap.get(channel);
		final List<Client> subscribers = channelsClientsMap.get(channel);
		if(subscribers ==null ){
			return;
		}
				List<Client> toRemove = new ArrayList<Client>();
				
				for(Client s : subscribers){
					
					if(s.getUserName().equals(userName)){
						continue;
					}
					synchronized (s) {
						AsyncContext aCtx = s.getaCtx();
						try {
							aCtx.getResponse().getOutputStream().print("logedout#"+userName);
							aCtx.getResponse().flushBuffer(); //so the client gets it NOW
							System.out.println("Notify to :"+s.getUserName());
						} catch (Exception e) {
							
							System.err.println("failed to send to client - removing from list of subscribers on this channel");
							e.printStackTrace();
							toRemove.add(s);
						}
					}
				}
				
				// remove the failed subscribers from the model in app scope, not our copy of them
				synchronized (subscribers) {
					subscribers.removeAll(toRemove);
					
				}

				
			}
	
}
