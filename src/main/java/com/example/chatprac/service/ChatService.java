package com.example.chatprac.service;

import com.example.chatprac.dto.ChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

// 추후 DB 와 연결 시 Service 와 Repository(DAO) 로 분리 예정
@Data
@Service
@Slf4j
public class ChatService {

    private final ObjectMapper mapper;
    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init(){
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    public List<ChatRoom> findAllRoom(){
        // 채팅방 생성 순서를 최근순으로 반환
        List chatRooms = new ArrayList<>(chatRoomMap.values());

        return chatRooms;
    }

    // roomId 기준으로 채팅방 찾기
    public ChatRoom findRoomById(String roomId){
        return chatRoomMap.get(roomId);
    }

    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName){
        String roomId = UUID.randomUUID().toString();

        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .roomName(roomName)
                .build();
        chatRoomMap.put(roomId, room);

        return room;
    }

    public <T> void sendMessage(WebSocketSession session, T message){
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));;
        }catch (IOException e){
            log.error(e.getMessage(),e);
        }
    }

    // 채팅방 인원+1
    public void plusUserCnt(String roomId){
        ChatRoom room = chatRoomMap.get(roomId);
        log.info(room.toString());
        room.setUserCount(room.getUserCount() + 1);
    }

    // 채팅방 인원-1
    public void minusUserCnt(String roomId){
        ChatRoom room = chatRoomMap.get(roomId);
        room.setUserCount(room.getUserCount() - 1);
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(String roomId, String userName){
        ChatRoom room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();

        // 아이디 중복 확인 후 userList 에 추가
        room.getUserlist().put(userUUID, userName);
        return userUUID;
    }

    // 채팅방 유저 이름 중복확인
    public String isDuplicateName(String roomId, String userName){
        ChatRoom room = chatRoomMap.get(roomId);
        String tmp = userName;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙여서 나온다.
        // 이때 랜덤한 숫자를 붙였을 때 getUserList 안에 있는 닉네임이라면 다시 랜덤한 숫자 적용
        while(room.getUserlist().containsValue(tmp)){
            int ranNum = (int)(Math.random()*100)+1;
            tmp = userName+ranNum;
        }
        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        room.getUserlist().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        return room.getUserlist().get(userUUID);
    }

    // 채팅방 전체 userList 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();
        ChatRoom room = chatRoomMap.get(roomId);

        // hashmap 을 for문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 리턴
        room.getUserlist().forEach((key, value) -> list.add(value));
        return list;
    }
}
