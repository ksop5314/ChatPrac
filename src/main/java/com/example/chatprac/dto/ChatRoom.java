package com.example.chatprac.dto;

import com.example.chatprac.service.ChatService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// Stomp 를 통해 pub/sub 를 사용하면 구독자 관리가 알아서 된다!!
// 따라서 따로 세션 관리를 하는 코드를 작성할 필도 없고,
// 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요가 없다!

@Data
@Slf4j
public class ChatRoom {

    private String roomId;
    private String roomName;
    private Set<WebSocketSession> sessions = new HashSet<>();
    private long userCount;

    private HashMap<String, String> userlist = new HashMap<String, String>();

    @Builder
    public ChatRoom(String roomId,String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public void handleAction(WebSocketSession session, ChatDTO message, ChatService service){
        //message 에 담긴 타입을 확인한다.
        //이때 message 에서 getType 으로 가져온 내용이
        //chatDto 의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
        if(message.getType().equals(ChatDTO.MessageType.ENTER)){
            //sessions 에 넘어온 session 을 담고,
            sessions.add(session);

            //message 에는 입장하였다는 메시지를 띄워줍니다.
            message.setMessage(message.getSender() + " 님이 입장하였습니다.");
            sendMessage(message,service);
        } else if (message.getType().equals(ChatDTO.MessageType.TALK)) {
            message.setMessage(message.getMessage());
            sendMessage(message,service);
        }
    }
    public <T> void sendMessage(T message, ChatService service){
        sessions.parallelStream().forEach(sessions -> service.sendMessage(sessions,message));
    }


}
