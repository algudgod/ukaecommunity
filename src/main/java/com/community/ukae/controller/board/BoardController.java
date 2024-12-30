package com.community.ukae.controller.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("boardList")
    public String boardList(@RequestParam String mainCategory,
                            @RequestParam String subCategory,
                            Model model) {

        // BoardCategory Enum 객체 찾기
        BoardCategory boardCategory = Arrays.stream(BoardCategory.values())
                .filter(category -> category.getMainCategory().equals(mainCategory) &&
                        category.getSubCategory().equals(subCategory))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));

        model.addAttribute("mainCategory", mainCategory);
        model.addAttribute("subCategory", boardCategory);

        List<BoardRequestDTO> boards = boardService.boardList(mainCategory, subCategory);
        model.addAttribute("boards", boards);  // 게시글 리스트

        return "board/boardList";  // HTML 템플릿 이름


    }

}
