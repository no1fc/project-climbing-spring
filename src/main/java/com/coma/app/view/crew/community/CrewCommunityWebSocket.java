package com.coma.app.view.crew.community;

import com.coma.app.biz.crew_board.Crew_boardDTO;
import com.coma.app.biz.crew_board.Crew_boardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@ServerEndpoint(value = "/chat/{member_id}",configurator = SpringConfigurator.class)
public class CrewCommunityWebSocket {
    //SpringConfigurator는 Spring 컨텍스트를 사용하여 WebSocket 엔드포인트 인스턴스를 생성하는 기능 제공
    //스프링의 DI 주입 기능 제공
    //@ServerEndpoint와 SpringConfigurator를 함께 사용하면,
    // SpringConfigurator가 WebSocket 엔드포인트 클래스가 Spring의 ApplicationContext 내에서 관리되는 빈(Bean)으로 동작하게 만듭

    @Autowired
    private Crew_boardService crew_boardService;

    private static final List<Session> chat_room = new ArrayList<>(); // 현재 채팅방 사용자 목록

    private static final ReentrantLock lock = new ReentrantLock(true);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen // WebSocket 연결 열기
    public void open(Session memberSession, @PathParam("member_id") String member_id) {
        log.info("CrewCommunityWebSocket.open : 실시간 채팅 연결");
        chat_room.add(memberSession); // 채팅방에 사용자 추가
        log.info(memberSession.getId());
        log.info(member_id);
        for (Session user : chat_room) {
            log.info("chat_room의 사용자아이디 = [{}]", user.getId());
        }
        try {
            List<Crew_boardDTO> datas = crew_boardService.selectAllNEW10(new Crew_boardDTO());
            if (!(datas.isEmpty() || datas == null)) {
                for (int i = datas.size() - 1; i >= 0; i--) {
                    broadcastMessage(createJsonMessage(datas.get(i).getCrew_board_writer_id(),
                            datas.get(i).getCrew_board_content(),
                            datas.get(i).getCrew_board_writer_profile(),
                            datas.get(i).getCrew_board_writer_name()));
                    log.info("selectAll 결과 data = [{}]", datas.get(i));
                }
            }
        } catch (Exception e) {
            //어떤 예외가 발생하더라도 웹소켓 연결 유지
            e.printStackTrace();
            return;
        }

        // 모든 클라이언트에게 입장 메시지 전송
        String msg = member_id + "님이 입장하였습니다";
        broadcastMessage(createSystemMessage(msg));
    }

    @OnMessage // 메시지 받기
    public void getMsg(String msg, @PathParam("member_id") String member_id) {
        log.info("전달 메세지 [{}] [{}]", member_id, msg);
        // JSON 문자열을 Crew_boardDTO 객체로 변환
        try {
            Crew_boardDTO crew_boardDTO = objectMapper.readValue(msg, Crew_boardDTO.class);
            // 메시지를 DB에 저장
            crew_boardService.insert(crew_boardDTO);
        } catch (Exception e){
            //어떤 예외가 발생하더라도 웹소켓 연결 유지
            e.printStackTrace();
            return;
        }

        // 모든 클라이언트에게 메시지 전송
        broadcastMessage(msg);
    }

    @OnClose // WebSocket 연결 닫기
    public void onClose(Session receiveSession, @PathParam("member_id") String member_id) {
        // 현재 websocket 사용자 리스트에서 현재 사용자 제거
        chat_room.remove(receiveSession);
        log.info("CrewCommunityWebSocket.onClose : 실시간 채팅 연결 해제");
        // 모든 클라이언트에게 퇴장 메시지 전송
        String msg = member_id + "님이 채팅방을 나갔습니다";
        broadcastMessage(createSystemMessage(msg));
    }

    private void broadcastMessage(String msg) {
        //동기화를 통해 경쟁 상태(race condition) 회피
        lock.lock(); // 락 획득
        try {
            for (Session session : chat_room) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            lock.unlock(); // 반드시 락 해제
        }
        //java.util.concurrent.locks 패키지의 고유한 동기화 도구
        //ReentrantLock 객체는 기본적으로 비공정 락(nonstrict fairness lock)
        //공정성 정책을 설정하여 스레드가 락을 획득하는 순서 제어, 공정성을 설정시 가장 오래 대기한 스레드가 락 획득

        //lock() : 락을 획득할 때까지 현재 스레드는 대기 상태
        //tryLock() : 스레드는 즉시 락을 얻으려 시도, 성공시 true, 실패시 false 반환
        //tryLock(long time, TimeUnit unit) : 지정된 시간 동안 락 획득 시도, 성공시 true, 시간 초과 시 false 반환
        //unlock() : 락 해제 락을 획득한 스레드만 호출 해야 하며, 그렇지 않으면 IllegalMonitorStateException 발생
    }

    private String createJsonMessage(String member_id, String content, String profileUrl, String member_name) {
        String jsonMessage = """
                {"crew_board_writer_id":"%s", "crew_board_content":"%s", "crew_board_writer_profile":"%s", "crew_board_writer_name":"%s"}""";
        return String.format(jsonMessage, member_id, content, profileUrl, member_name);
    }

    private String createSystemMessage(String content) {
        String systemMessage = """
                {"type": "system","content": "%s"}
                """;
        return String.format(systemMessage, content);
    }
}
