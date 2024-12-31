package com.community.ukae.service.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.repository.board.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
                board.getBoardNo(),  // boardNo를 포함
                board.getMainCategory(),
                board.getSubCategory(),
                board.getTitle(),
                board.getUser().getNickname(),
                board.getContent(),
                board.getViewCount(),
                board.getCreateDate()

        );
    }

    // mainCategory와 subCategory에 해당하는 BoardCategory(Enum 상수)를 반환
    public BoardCategory findBoardCategory(String mainCategory, String subCategory) {
        return Arrays.stream(BoardCategory.values())
                .filter(category -> category.getMainCategory().equals(mainCategory) &&
                        category.getSubCategory().equals(subCategory))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));
    }

    // 모든 BoardCategory 를 mainCategory 기준으로 그룹화하여 반환
    // Map Key: mainCategory
    // Map Value: 해당 mainCategory 에 속하는 BoardCategory 객체 리스트
    public Map<String, List<BoardCategory>> getAllCategories() {
        return Arrays.stream(BoardCategory.values())
                .collect(Collectors.groupingBy(BoardCategory::getMainCategory));
    }

    public void addBoard(BoardRequestDTO boardRequest, User user) {

        Board board = new Board();
        board.setMainCategory(boardRequest.getMainCategory());
        board.setSubCategory(boardRequest.getSubCategory());
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setUser(user);

        boardRepository.save(board);
    }
}