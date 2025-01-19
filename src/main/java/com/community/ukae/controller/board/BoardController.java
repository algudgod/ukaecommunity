package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.dto.board.BoardResponseDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.enums.BoardTag;
import com.community.ukae.service.board.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    private final BoardService boardService;

    // 게시판 목록
    @GetMapping("boardList")
    public String boardList(@RequestParam String mainCategory,
                            @RequestParam String subCategory,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        // 대/소분류 카테고리 조회
        BoardCategory boardCategory = boardService.findBoardCategory(mainCategory, subCategory);

        model.addAttribute("mainCategory", mainCategory); // @RequestParam 으로 전달받은 mainCategory String 값
        model.addAttribute("subCategory", boardCategory); // BoardCategory Enum 객체

        // 게시판 목록 가져오기 (페이지네이션 적용)
        List<BoardResponseDTO> boardResponse = boardService.getBoardWithCategoryNumbers(mainCategory, subCategory, page, size);
        model.addAttribute("boards", boardResponse);
        // 모든 카테고리 정보
        model.addAttribute("boardCategories", boardService.getAllCategories());

        // 새 글 수 계산 (오늘 작성된 게시글 수)
        int todayBoardCount = boardService.countTodayBoardByCategory(mainCategory, subCategory);
        model.addAttribute("todayBoardCount",todayBoardCount);
        // 전체 글 수 계산 (현재 카테고리 내 전체 게시글 수)
        int totalBoardCount = boardResponse.size();
        model.addAttribute("totalBoardCount",totalBoardCount);

        // 전체 페이지 수 계산 (전체 글 수 ÷ 페이지당 게시글 수)
        int totalPages = boardService.getTotalPages(mainCategory, subCategory, size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // 페이지네이션 URL 생성 (페이지 이동 시 사용)
        String pageUrl = "/board/boardList?mainCategory=" + mainCategory + "&subCategory=" + subCategory + "&page=";
        model.addAttribute("pageUrl", pageUrl);

        return "board/boardList";

    }

    // 게시글 쓰기 form
    @GetMapping("addBoardForm")
    public String addBoardForm(@RequestParam String mainCategory,
                               @RequestParam String subCategory,
                               @RequestParam(required = false) String tag,
                               HttpSession session,
                               Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        // subCategory 값을 BoardCategory Enum 매핑
        BoardCategory boardCategory = BoardCategory.valueOf(subCategory);
        // tag 값을 BoardTag Enum 매핑 (null 허용)
        BoardTag boardTag = (tag != null) ? BoardTag.valueOf(tag) : null;

        BoardRequestDTO boardRequest = new BoardRequestDTO();
        boardRequest.setMainCategory(mainCategory);
        boardRequest.setSubCategory(boardCategory.name());
        boardRequest.setTag(boardTag != null ? boardTag.name() : null);
        boardRequest.setNickname(user.getNickname());

        model.addAttribute("boardRequest", boardRequest);
        model.addAttribute("subCategory", boardCategory);
        model.addAttribute("tag", boardTag);
        model.addAttribute("tags", BoardTag.values());

        model.addAttribute("boardCategories", boardService.getAllCategories());

        return "board/addBoardForm";
    }

    // 게시글 쓰기
    @PostMapping("addBoard")
    public String addBoard(BoardRequestDTO boardRequest,
                           HttpSession session,
                           Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        logger.info("서버로 전달된 tag 값: {}", boardRequest.getTag());

        try {
            boardService.addBoard(boardRequest, user); // 서비스 호출
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("errorMessage", e.getMessage());
            BoardCategory boardCategory = BoardCategory.valueOf(boardRequest.getSubCategory());
            model.addAttribute("subCategory", boardCategory);
            model.addAttribute("boardRequest", boardRequest);
            return "board/addBoardForm";
        }

        return "redirect:/board/boardList?mainCategory=" + boardRequest.getMainCategory() +
                "&subCategory=" + boardRequest.getSubCategory();
    }

    // 게시글 상세 조회
    @GetMapping("detail/{boardNo}")
    public String getBoardDetail(@PathVariable int boardNo, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        boardService.updateViewCount(boardNo,user);

        try {
            BoardResponseDTO boardResponse = boardService.findBoardByBoardNo(boardNo);
            model.addAttribute("board", boardResponse);
            model.addAttribute("boardCategories", boardService.getAllCategories());
            return "board/boardDetail";
        } catch (NoSuchElementException e) {
            return "redirect:/error/notFound";
        }
    }

    // 게시글 수정 form
    @GetMapping("editBoard/{boardNo}")
    public String editBoardForm(@PathVariable int boardNo,
                                HttpSession session, Model model) {
        // 사용자 인증 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        // 게시글 조회
        BoardResponseDTO boardResponse = boardService.findBoardByBoardNo(boardNo);
        if (boardResponse == null) {
            logger.warn("해당 게시글을 찾을 수 없습니다.: boardNo={}", boardNo);
            return "redirect:/error/notFound";
        }
        // 수정 폼 데이터
        BoardRequestDTO boardRequest = boardService.getEditBoardForm(boardNo, user);

        BoardCategory boardCategory = BoardCategory.valueOf(boardResponse.getSubCategory());
        BoardTag boardTag = (boardResponse.getTag() != null) ? BoardTag.valueOf(boardResponse.getTag()) : null;
        logger.info("게시글 수정 폼 초기화: boardNo={}, subCategory={}, tag={}", boardNo, boardCategory, boardTag);

        model.addAttribute("board", boardResponse);
        model.addAttribute("boardRequest", boardRequest);
        model.addAttribute("subCategory", boardCategory);
        model.addAttribute("boardCategories", boardService.getAllCategories());
        model.addAttribute("tag", boardTag);
        model.addAttribute("tags", BoardTag.values());

        logger.info("게시글 수정 폼 가져오기 성공: boardNo={}", boardNo);

        return "board/editBoardForm";
    }

    // 게시글 수정
    @PostMapping("updateBoard")
    public String updateBoard(@ModelAttribute BoardRequestDTO boardRequest,
                              HttpSession session, Model model) {
        // 사용자 인증 체크
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        try {
            // 서비스 레이어에서 유효성 검사 및 업데이트 처리
            boardService.updateBoard(boardRequest, user);
            return "redirect:/board/detail/" + boardRequest.getBoardNo();
        } catch (IllegalArgumentException e) {
            logger.warn("유효성 검사 실패 또는 권한 문제: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("게시글 업데이트 실패: {}", e.getMessage());
        }

        BoardCategory boardCategory = BoardCategory.valueOf(boardRequest.getSubCategory());
        BoardTag boardTag = (boardRequest.getTag() != null) ? BoardTag.valueOf(boardRequest.getTag()) : null;
        logger.info("updateBoard 게시글 수정 폼 초기화: boardNo={}, subCategory={}, tag={}", boardRequest.getBoardNo(), boardCategory, boardTag);

        model.addAttribute("boardRequest", boardRequest);
        model.addAttribute("subCategory", boardCategory);
        model.addAttribute("tag", boardTag);
        model.addAttribute("boardCategories", boardService.getAllCategories());
        model.addAttribute("tags", BoardTag.values());

        return "board/editBoardForm";
    }

    // 게시글 삭제
    @PostMapping("/deleteBoard/{boardNo}")
    public String deleteBoard(@PathVariable int boardNo,
                              HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        try {
            BoardResponseDTO board = boardService.findBoardByBoardNo(boardNo);
            boardService.deleteBoard(boardNo, user);
            logger.info("게시글 삭제 성공: boardNo={}, mainCategory={}, subCategory={}, 삭제자={}",
                    boardNo, board.getMainCategory(), board.getSubCategory(), user.getLoginId());
            return "redirect:/board/boardList?mainCategory=" + board.getMainCategory() +
                    "&subCategory=" + board.getSubCategory();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            logger.warn("deleteBoard 실패: boardNo={}, error={}", boardNo, e.getMessage());
            return "redirect:/error/notFound";
        }

    }


}
