package com.community.ukae.service.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardRequestDTO> boardList(String mainCategory, String subCategory) {

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
                board.getUser().getNickname(),
                board.getContent(),
                board.getViewCount(),
                board.getCreateDate()

        );
    }

    // mainCategory와 subCategory에 해당하는 BoardCategory(Enum 상수)를 반환 (특정조건)
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
                .collect(Collectors.groupingBy(
                        BoardCategory::getMainCategory, // 그룹화 기준
                        LinkedHashMap::new,             // 순서를 보장하는 Map 생성
                        Collectors.toList()             // 그룹화된 항목을 List 로 수집
                ));
    }

    public void addBoard(BoardRequestDTO boardRequest, User user) {

        if (boardRequest.getTitle().length() < 3 || boardRequest.getTitle().length() > 100) {
            throw new IllegalArgumentException("제목은 3자 이상, 100자 이하이어야 합니다.");
        }
        if (boardRequest.getContent().length() < 10 || boardRequest.getContent().length() > 2000) {
            throw new IllegalArgumentException("내용은 최소 10자 이상, 2000자 이하이어야 합니다.");
        }

        Board board = new Board();
        board.setMainCategory(boardRequest.getMainCategory());
        board.setSubCategory(boardRequest.getSubCategory());
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setUser(user);

        boardRepository.save(board);
    }
}