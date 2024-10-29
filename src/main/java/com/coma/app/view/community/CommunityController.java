package com.coma.app.view.community;


import java.util.HashMap;
import java.util.Map;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.coma.app.biz.board.BoardDTO;
import com.coma.app.biz.board.BoardService;


@Slf4j
@Controller
public class CommunityController{
    @Autowired
    private BoardService boardService;


    @GetMapping(value="/community.do")
    public String community(BoardDTO boardDTO, Model model) {

        String search_Keyword = boardDTO.getSearch_keyword(); // 검색 키워드

        log.info("community.searchKeyword : ["+search_Keyword+"]");

        int pageNum = boardDTO.getPage();//요거 필요
        int boardSize = 10; // 한 페이지에 표시할 게시글 수 설정
        int minBoard = 1; // 최소 게시글 수 초기화

        if (pageNum <= 0) { // 페이지가 0일 때 (npe방지)
            pageNum = 1;
        }
        minBoard = ((pageNum - 1) * boardSize); // 최소 게시글 번호 계산
        int listNum = 0; // 게시글 총 개수를 저장할 변수 초기화


        log.info("community.pageNum: ["+pageNum+"]");

        // 검색 조건과 키워드가 있는지 확인

        List<BoardDTO> boardList = null;
        // 검색 조건과 키워드가 있는지 확인
        if(search_Keyword != null) {
            // 글 검색 부분
            if(search_Keyword.equals("SEARCH_ID")) {
                // 아이디로 검색했을 때
//                boardDTO.setSearch_content("BOARD_ONE_SEARCH_ID_COUNT");
               log.info("community.boardCount : ["+boardDTO+"]");//로그

                listNum = this.boardService.selectOne(boardDTO).getTotal();//게시글의 전체 개수
                log.info("community.listNum: ["+listNum+"]");//로그

                //boardDTO.setBoard_condition("BOARD_ALL_SEARCH_PATTERN_ID"); // 아이디검색 컨디션
                // 게시글 목록 조회
                boardList = this.boardService.selectAllSearchPatternId(boardDTO); // 설정된 조건으로 게시글 목록 조회

                // selectOne 메소드를 호출하여 검색 조건에 맞는 게시글 수를 가져옴
                log.info(" community.boardDTO : ["+boardDTO+"]");//로그
            }
            else if(search_Keyword.equals("SEARCH_WRITER")) {
                // 작성자로 검색했을 때

//                boardDTO.setBoard_condition("BOARD_ONE_SEARCH_NAME_COUNT");
                log.info("(community.boardCount : ["+boardDTO+"]");//로그

                listNum = this.boardService.selectOne(boardDTO).getTotal();
                log.info("community.listNum : ["+listNum+"]");//로그

                //boardDTO.setBoard_condition("BOARD_ALL_SEARCH_NAME"); // 작성자 검색 컨디션
                // 게시글 목록 조회
                boardList = this.boardService.selectAllSearchName(boardDTO); // 설정된 조건으로 게시글 목록 조회

                log.info("community.boardDTO : ["+boardDTO+"]");//로그
            }
            else if(search_Keyword.equals("SEARCH_TITLE")) {
                // 글 제목으로 검색했을 때
//                boardDTO.setSearch_content("BOARD_ONE_SEARCH_TITLE_COUNT");
               // log.info("community.boardDTO : ["+boardDTO+ "]");//로그

                listNum = this.boardService.selectOne(boardDTO).getTotal();
                log.info("community.listNum : ["+listNum+"]");//로그

                //boardDTO.setBoard_condition("BOARD_ALL_SEARCH_TITLE"); // 제목 검색 컨디션
                // 게시글 목록 조회
                boardList = this.boardService.selectAllSearchTitle(boardDTO); // 설정된 조건으로 게시글 목록 조회

                log.info("community.boardDTO : ["+boardDTO+"]");//로그
            }
        }
        //검색어가 없다면 BOARD_ALL
        else {
            // 검색 조건이 없는 경우 전체 검색
//            boardDTO.setBoard_condition("BOARD_ONE_COUNT");
            listNum = this.boardService.selectOneCount(boardDTO).getTotal();
            log.info("community.listNum : ["+listNum+"]");

            //boardDTO.setBoard_condition("BOARD_ALL"); // 전체 게시글 조회 컨디션
            // 게시글 목록 조회
            boardList = this.boardService.selectAll(boardDTO); // 설정된 조건으로 게시글 목록 조회
        }

        // 게시글을 페이지 단위로 잘라서 조회해야 함
        // boardDTO에 minPage와 maxPage 값을 설정하여 조회 범위를 지정해야 함

        boardDTO.setBoard_min_num(minBoard);


        model.addAttribute("BOARD", boardList); // 조회된 게시글 목록을 요청 객체에 저장
        model.addAttribute("total", listNum); // 전체 글 개수
        model.addAttribute("page", pageNum); // 현재 페이지 번호



        return "views/communityRegions"; // 설정된 페이지로 이동
    }

    @GetMapping("/location.do")
    public String local(Model model, BoardDTO boardDTO) {

        String Location = boardDTO.getBoard_location(); // 지역검색
//        String title = boardDTO.getBoard_title();
        // 지역명 맵핑
        String location = Location(Location);
        log.info("location.Location : ["+Location+"]");

        int pageNum = boardDTO.getPage();//요거 필요
        int boardSize = 10; // 한 페이지에 표시할 게시글 수 설정
        int minBoard = 1; // 최소 게시글 수 초기화
        if (pageNum <= 0) { // 페이지가 0일 때 (npe방지)
            pageNum = 1;
        }
        minBoard = ((pageNum - 1) * boardSize); // 최소 게시글 번호 계산
        int listNum = 0; // 게시글 총 개수를 저장할 변수 초기화



        log.info("location.pageNum : ["+pageNum+"]");
        log.info("location.minBoard : [" + minBoard+"]");

        boardDTO.setBoard_min_num(minBoard);
       boardDTO.setSearch_keyword(location);
//        boardDTO.setBoard_title(title);
//        boardDTO.setBoard_condition("BOARD_ALL_SEARCH_TITLE");
        log.info("location.boardDTO : [" + boardDTO+"]");
        List<BoardDTO> datas = this.boardService.selectAllSearchTitle(boardDTO);
        log.info("location.datas : [" + datas+"]");

//        boardDTO.setBoard_condition("BOARD_ONE_SEARCH_TITLE_COUNT");
        BoardDTO boardCount = this.boardService.selectOneSearchTitleCount(boardDTO);
        log.info("location.boardCount : [" + boardCount+"]");

        listNum = boardCount.getTotal();


        model.addAttribute("page", pageNum);
        model.addAttribute("total", listNum);
        model.addAttribute("BOARD",datas);


        return "views/localCommunity";
    }
    /* 뷰에서 전달받은 지역 값을 실제 지역명으로 변환하는 함수
     */
    public String Location(String view_Location) {
        Map<String, String> locationMap = new HashMap<String, String>();

        locationMap.put("SEOUL", "서울특별시");
        locationMap.put("GYEONGGI", "경기도");
        locationMap.put("INCHEON", "인천광역시");
        locationMap.put("SEJONG", "세종특별자치도");
        locationMap.put("BUSAN", "부산광역시");
        locationMap.put("DAEGU", "대구광역시");
        locationMap.put("DAEJEON", "대전광역시");
        locationMap.put("GWANGJU", "광주광역시");
        locationMap.put("ULSAN", "울산광역시");
        locationMap.put("CHUNGCHEONGNAMDO", "충청남도");
        locationMap.put("CHUNGCHEONGBUKDO", "충청북도");
        locationMap.put("JEONLANAMDO", "전라남도");
        locationMap.put("JEONLABUKDO", "전라북도");
        locationMap.put("GYEONGSANGNAMDO", "경상남도");
        locationMap.put("GYEONGSANGBUKDO", "경상북도");
        locationMap.put("GANGWONDO", "강원도");
        locationMap.put("CHUNGNAM", "충청남도");

        return locationMap.getOrDefault(view_Location, "서울특별시"); // 기본값은 서울특별시
        //getOrDefault  Map 인터페이스에서 제공하는 메서드로,
        //특정 키에 해당하는 값을 반환하되,
        //만약 그 키가 존재하지 않으면 기본값을 반환하는 역할을 합니다.
    }






}
