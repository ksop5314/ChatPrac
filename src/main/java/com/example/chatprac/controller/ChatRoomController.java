package com.example.chatprac.controller;

import com.example.chatprac.dto.ChatRoom;
import com.example.chatprac.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    // ChatService Vean 가져오기
    private final ChatService chatService;

    // 채팅 리스트 화면
    // / 로 요청이 들어오면 전체 채팅룸 리스트를 담아서 return
    @GetMapping("/")
    public String goChatRoom(Model model){

        model.addAttribute("list", chatService.findAllRoom());
        return "roomList";
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 / 로 return
    @PostMapping("/chat/createRoom")
    public String createRoom(@RequestParam String name, RedirectAttributes rttr){
        ChatRoom room = chatService.createChatRoom(name);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인 후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatRoom 으로 보낸다.
    @GetMapping("/chat/room")
    public String roomDetail(Model model, String roomId){
        model.addAttribute("room", chatService.findRoomById(roomId));
        return "chatRoom";
    }
}
