
package org.chatapp.listeners;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.chatapp.servlets.LoginServlet;
import org.chatapp.useroperation.Client;

@WebListener
public class ContextInitialiser implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {

		// **************************
		// stick a new model into the application scope
		// **************************

		ServletContext appScope = sce.getServletContext();
		//list of online clients for channel 
		final Map<String, List<Client>> onlineClients = Collections.synchronizedMap(new HashMap<String, List<Client>>());
		appScope.setAttribute(LoginServlet.CLIENTS, onlineClients);
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
