package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                           @RequestParam(value = "images", required = false) List<MultipartFile> images,
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

        // 이미지 개수 제한 검증
        if (images != null && images.size() > 3) {
            model.addAttribute("error", "이미지는 최대 3개까지만 첨부할 수 있습니다.");
            return "board/addBoardForm";
        }

        try {
            boardService.addBoard(boardRequest, user, images); // 서비스 호출
        } catch (IllegalArgumentException | IOException e) {
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
