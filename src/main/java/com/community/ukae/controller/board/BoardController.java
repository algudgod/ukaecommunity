package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.service.board.BoardService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // 글쓰기 form
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

    // 글 쓰기
    @PostMapping("addBoard")
    public String addBoard(@Valid BoardRequestDTO boardRequest,
                           BindingResult result,
                           HttpSession session,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("titleError", result.hasFieldErrors("title") ?
                    "제목은 3자 이상, 100자 이하입니다." : null);
            model.addAttribute("contentError", result.hasFieldErrors("content") ?
                    "내용은 최소 5자 이상, 2000자 이하입니다." : null);
            model.addAttribute("boardRequest", boardRequest);
            return "board/addBoardForm";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);

        boardService.addBoard(boardRequest, user);

        return "redirect:/board/boardList?mainCategory=" + encode(boardRequest.getMainCategory()) +
                "&subCategory=" + encode(boardRequest.getSubCategory());
    }
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 실패", e);
        }
    }

}
