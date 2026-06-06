package com.flowstudy.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StudyRoomController {

    private final SimpMessagingTemplate messagingTemplate;

    public StudyRoomController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 接收狀態更新並廣播給房間內所有人
    @MessageMapping("/room.statusUpdate")
    public void updateStatus(@Payload RoomStatusMessage message) {
        // 將更新推播到特定的房間頻道
        messagingTemplate.convertAndSend("/topic/room/" + message.roomId(), message);
    }

    // 處理「拍一拍」喚醒隊友 (點對點推送)
    @MessageMapping("/room.nudge")
    public void nudgeTeammate(@Payload NudgeMessage message) {
        // 推送到特定使用者的專屬頻道
        messagingTemplate.convertAndSendToUser(
            message.targetUserId(), "/queue/nudges", message
        );
    }

    // 內部 DTO (對應 Contract)
    public record RoomStatusMessage(String roomId, String userId, String status) {}
    public record NudgeMessage(String fromUserId, String targetUserId, String message) {}
}