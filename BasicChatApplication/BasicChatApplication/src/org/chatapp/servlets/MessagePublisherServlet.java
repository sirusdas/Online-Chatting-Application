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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chatapp.constants.AppConstatns;
import org.chatapp.useroperation.Client;

@WebServlet(name = "publishServlet", urlPatterns = { "/publish" }, asyncSupported = true)
public class MessagePublisherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// *************************
		// this servlet simply spawns a thread to send its message to all subscribers.
		// this servlet keeps the connection to its client open long enough to tell it 
		// that it has published to all subscribers.
		// *************************
		
		// add a pipe character, so that the client knows from where the newest model has started.
		// if messages are published really quick, its possible that the client gets two at
		// once, and we dont want it to be confused!  these messages also arrive at the 
		// ajax client in readyState 3, where the responseText contains everything since login,
		// rather than just the latest chunk.  so, the client needs a way to work out the 
		// latest part of the message, containing the newest version of the model it should 
		// work with.  might be better to return XML or JSON here!
		final String msg =  request.getParameter("message") ;

		// to which channel should it publish?  in prod, we would check authorisations here too!
		final String channel = request.getParameter("channel");

		// get the application scoped model, and copy the list of subscribers, so that the 
		// long running task of publishing doesnt interfere with new logins
		ServletContext appScope = request.getServletContext();
		@SuppressWarnings("unchecked")
		final Map<String, List<Client>> clients = (Map<String, List<Client>>) appScope.getAttribute(LoginServlet.CLIENTS);
		final List<Client> subscribers = new ArrayList<Client>(clients.get(channel));

		// we are going to hand the longer running work off to a new thread, 
		// using the container and the async support it provides.
		final AsyncContext publisherAsyncCtx = request.startAsync();
		
		// aknowledge to the caller that we are starting to publish...
		response.getWriter().write("<html><body>Started publishing<br>");
		response.flushBuffer(); //tell the publisher NOW

		
		
		// here is the logic for publishing - it will be passed to the container for execution sometime in the future
		Runnable r = new Runnable(){
			public void run() {

				long start = System.currentTimeMillis();
				String[] uNameMsg = msg.split("#");
				
				if(uNameMsg.length <=2){
					return;
				}
				
				String msgType =uNameMsg[0];
				String msgFromUser =uNameMsg[1];
				String userNameToSend =uNameMsg[2];
				
				
				//chatmessage#from#to#message=message
				//keep a list of failed subscribers so we can remove them at the end
				
				List<Client> toRemove = new ArrayList<Client>();
				
				
				boolean isUserfound =false;//required to check user is found or not
				for(Client s : subscribers){
					
					//if message type is CHATMESSAGE then send the recieved message to only one user other wise send it to all
					if( msgType.equals(AppConstatns.CHATMESSAGE) ){
						
						if(s.getUserName().equals(userNameToSend)){
							isUserfound =true;
						}else{
							continue;
						}
					}
					if( msgType.equals(AppConstatns.GROUPCHATMESSAGE) ){
						if(s.getUserName().equals(msgFromUser)){
							continue;
						}
					}
					
					synchronized (s) {
						AsyncContext aCtx = s.getaCtx();
						try {
							aCtx.getResponse().getOutputStream().print(msg);
							aCtx.getResponse().flushBuffer(); //so the client gets it NOW
							
						} catch (Exception e) {
							
							System.err.println("failed to send to client - removing from list of subscribers on this channel");
							e.printStackTrace();
							toRemove.add(s);
							//tell other clients about this use is log out.
						}
					}
					if(isUserfound == true){
						break;
					}
				}
				
				// remove the failed subscribers from the model in app scope, not our copy of them
				synchronized (clients) {
					clients.get(channel).removeAll(toRemove);
				}

				//log success
				long ms = System.currentTimeMillis() - start;
				String ok = "finished publish of " + msg + " to channel " + channel + " in " + ms + " ms.";
				System.out.println(ok);
				
				//aknowledge to the publishing client that we have finished.
				try {
					publisherAsyncCtx.getResponse().getWriter().write(ok + "</body></html>");
					publisherAsyncCtx.complete(); //we are done, the connection can be closed now
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		//start the async processing (using a pool controlled by the container)
		publisherAsyncCtx.start(r);
	}
}
