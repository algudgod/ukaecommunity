package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("boardList")
    public String boardList(@RequestParam String category,
                            @RequestParam String subCategory, Model model){

        List<BoardRequestDTO> boards = boardService.boardList(category, subCategory);

        model.addAttribute("category", category);  // 대분류 카테고리
        model.addAttribute("subCategory", subCategory);  // 서브카테고리
        model.addAttribute("boards", boards);  // 게시글 리스트

        return "board/boardList";  // HTML 템플릿 이름


    }

}
