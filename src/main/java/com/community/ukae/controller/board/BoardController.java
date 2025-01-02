package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.service.board.BoardService;
import com.community.ukae.utils.UrlEncodeUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        BoardCategory boardCategory = boardService.findBoardCategory(mainCategory, subCategory);

        model.addAttribute("mainCategory", mainCategory); // @RequestParam 으로 전달받은 mainCategory String 값
        model.addAttribute("subCategory", boardCategory); // BoardCategory Enum 객체

        List<BoardRequestDTO> boardRequest = boardService.getBoardWithCategoryNumbers(mainCategory, subCategory);
        model.addAttribute("boards",boardRequest);

        return "board/boardList";

    }

    // 게시글 쓰기 form
    @GetMapping("addBoardForm")
    public String addBoardForm(@RequestParam String mainCategory,
                               @RequestParam String subCategory,
                               HttpSession session,
                               Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        BoardRequestDTO boardRequest = new BoardRequestDTO();
        boardRequest.setMainCategory(mainCategory);
        boardRequest.setSubCategory(subCategory);
        boardRequest.setNickname(user.getNickname());

        model.addAttribute("boardRequest", boardRequest);
        model.addAttribute("boardCategories", boardService.getAllCategories());

        return "board/addBoardForm";
    }

    // 게시글 쓰기
    @PostMapping("addBoard")
    public String addBoard(@Valid BoardRequestDTO boardRequest,
                           BindingResult result,
                           HttpSession session,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("boardRequest", boardRequest);
            return "board/addBoardForm";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        try {
            boardService.addBoard(boardRequest, user);
            logger.info("게시글 작성 성공: {}", boardRequest.getTitle());
        } catch (IllegalArgumentException e) {
            logger.error("게시글 작성 실패: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("boardRequest", boardRequest);
            return "board/addBoardForm";
        }

        return "redirect:/board/boardList?mainCategory=" + UrlEncodeUtil.encode(boardRequest.getMainCategory()) +
                "&subCategory=" + UrlEncodeUtil.encode(boardRequest.getSubCategory());
    }


    // 게시글 상세 조회
    @GetMapping("detail/{boardNo}")
    public String getBoardDetail(@PathVariable int boardNo, HttpSession session, Model model){

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        BoardRequestDTO  board = boardService.findBoardByBoardNo(boardNo);

        model.addAttribute("board",board);
        model.addAttribute("boardCategories", boardService.getAllCategories());
        model.addAttribute("boardCategory", board.getMainCategory() + " > " + board.getSubCategory());

        return "board/boardDetail";

    }
}
