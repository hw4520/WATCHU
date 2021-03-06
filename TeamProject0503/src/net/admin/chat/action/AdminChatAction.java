package net.admin.chat.action;

import java.io.IOException; 
import java.util.Collections;
import java.util.Date;
import java.util.HashSet; 
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.OnClose; 
import javax.websocket.OnMessage; 
import javax.websocket.OnOpen; 
import javax.websocket.Session; 
import javax.websocket.server.ServerEndpoint; 
 
 
@ServerEndpoint("/AdminChatAction") 
public class AdminChatAction{  
   private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>()); 
   @OnMessage 
    public void onMessage(String message, Session session) throws IOException { 
       synchronized (clients) { 
          // Iterate over the connected sessions 
          // and broadcast the received message 
          for (Session client : clients) { 
             if (!client.equals(session)) { 
                client.getBasicRemote().sendText(message); 
             } 
          } 
       } 
    } 
    @OnOpen 
    public void onOpen(Session session) {
       clients.add(session); 
    } 
    @OnClose 
    public void onClose(Session session) {
       clients.remove(session); 
    }
 } 