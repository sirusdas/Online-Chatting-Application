package org.chatapp.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chatapp.useroperation.Client;

@WebServlet(name = "onlineUsersServlet", urlPatterns = { "/getOnlineUsersList" })
public class ListOfOnlineUsers extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String commaSepeparatedStr ="";
		ServletContext appScope = request.getServletContext();
		String channel = request.getParameter("channel");
		final Map<String, List<Client>> clients = (Map<String, List<Client>>) appScope.getAttribute(LoginServlet.CLIENTS);
		System.out.println(clients);
		if(clients.size()> 0){
			final List<Client> onlineClients = clients.get(channel);
			if(onlineClients !=null){
				for (Client client : onlineClients) {
					if(commaSepeparatedStr.equals("") ){
						commaSepeparatedStr = client.getUserName();
					}else{
						commaSepeparatedStr =commaSepeparatedStr+","+ client.getUserName();
					
					}
				}
			}
		}
		response.getWriter().write(commaSepeparatedStr);
		response.flushBuffer(); 

	}

}
