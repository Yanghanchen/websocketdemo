package hnu.yhc.websocket.service;

import hnu.yhc.websocket.data.Users;
import org.springframework.stereotype.Component;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chatroom/{userName}")
@Component
public class WebsocketServer {
    private Session session;
    private static int count=0;

    @OnOpen
    public void onOpen(Session session, @PathParam("userName")String userName) throws IOException {
        this.session=session;
        if(Users.user.get(userName)==null) {
            Users.user.put(userName, this);
            count++;
        }
        session.getBasicRemote().sendText("系统消息：当前在线人数:"+count);
    }

    @OnMessage
    public void onMessage(String msg) throws IOException {
        if(msg.startsWith("<font")){
            String user2=msg.substring(msg.indexOf("对")+1,msg.indexOf("悄悄"));
            WebsocketServer server2=Users.user.get(user2);
            if(server2!=null){
                server2.session.getBasicRemote().sendText(msg);
            }
            session.getBasicRemote().sendText(msg);
        }else {
            for (WebsocketServer server : Users.user.values()) {
                server.session.getBasicRemote().sendText(msg);
            }
        }
    }
}
