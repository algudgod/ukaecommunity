package com.community.ukae.service.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardRequestDTO> boardList(String mainCategory, String subCategory) {
        // 레포지토리에서 게시글 목록 가져오기
        List<Board> boards = boardRepository.findByMainCategoryAndSubCategory(mainCategory, subCategory);

        // Board 엔티티를 BoardRequestDTO로 변환
        return boards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환하는 메서드
    private BoardRequestDTO convertToDTO(Board board) {
        return new BoardRequestDTO(
                board.getBoardNo(),
                board.getMainCategory(),
                board.getSubCategory(),
                board.getTitle(),
                board.getUser().getNickname(), // 닉네임
                board.getContent(),
                board.getViewCount(),
                board.getCreateDate()
        );
    }


}