package com.campusconnect.campusconnect.call;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Lightweight WebRTC signaling relay. Media remains peer-to-peer; this server does not store audio/video. */
@Component
public class SignalingHandler extends TextWebSocketHandler {
 private final ObjectMapper mapper=new ObjectMapper();
 private final Map<String,Set<WebSocketSession>> rooms=new ConcurrentHashMap<>();
 private String room(JsonNode n){return n.hasNonNull("room")?n.get("room").asText():"";}
 @Override public void afterConnectionClosed(WebSocketSession s,CloseStatus status){rooms.values().forEach(set->set.remove(s));}
 @Override protected void handleTextMessage(WebSocketSession session,TextMessage message) throws Exception {
  JsonNode n=mapper.readTree(message.getPayload()); String room=room(n); if(room.isBlank())return;
  rooms.computeIfAbsent(room,k->ConcurrentHashMap.newKeySet()).add(session);
  for(WebSocketSession peer:rooms.get(room)) if(peer.isOpen()&&!peer.getId().equals(session.getId())) peer.sendMessage(message);
 }
}
