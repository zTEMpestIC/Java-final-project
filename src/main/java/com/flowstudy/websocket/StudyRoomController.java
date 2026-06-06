package com.flowstudy.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class StudyRoomController {

    private final SimpMessagingTemplate messagingTemplate;

    public StudyRoomController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 1. 房間廣播：接收狀態更新並廣播給房間內所有人
    // 前端發送至：/app/room.status
    @MessageMapping("/room.status")
    public void updateStatus(@Payload RoomStatusMessage message) {
        System.out.println("收到狀態更新：" + message.userId() + " -> " + message.status());
        
        // 將更新推播到該房間的專屬頻道 (前端需訂閱 /topic/room/{roomId})
        messagingTemplate.convertAndSend("/topic/room/" + message.roomId(), message);
    }

    // 2. 拍一拍：喚醒隊友 (精準推播)
    // 前端發送至：/app/room.nudge
    @MessageMapping("/room.nudge")
    public void nudgeTeammate(@Payload NudgeMessage message) {
        System.out.println("收到拍一拍：" + message.fromUserId() + " 拍了 " + message.targetUserId());
        
        // 推送到目標使用者的專屬頻道 (目標前端需訂閱 /topic/nudge/{targetUserId})
        messagingTemplate.convertAndSend("/topic/nudge/" + message.targetUserId(), message);
    }

    // --- 內部通訊用 DTO ---
    public record RoomStatusMessage(String roomId, String userId, String status, LocalDateTime timestamp) {
        public RoomStatusMessage {
            if (timestamp == null) timestamp = LocalDateTime.now();
        }
    }
    
    public record NudgeMessage(String fromUserId, String targetUserId, String message) {}
}