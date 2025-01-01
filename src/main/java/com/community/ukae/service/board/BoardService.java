package com.community.ukae.service.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

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
        if (boardRequest.getContent().length() < 5 || boardRequest.getContent().length() > 2000) {
            throw new IllegalArgumentException("내용은 최소 5자 이상, 2000자 이하이어야 합니다.");
        }

        Board board = new Board();
        board.setMainCategory(boardRequest.getMainCategory());
        board.setSubCategory(boardRequest.getSubCategory());
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setUser(user);

        boardRepository.save(board);
    }

    // 특정 키테고리의 게시글 목록을 카테고리별 고유 번호와 함께 반환
    public List<BoardRequestDTO> getBoardWithCategoryNumbers(String mainCategory, String subCategory){

        List<Object[]> rows = boardRepository.findByCategoryWithRowNumber(mainCategory, subCategory);

        for (Object[] row : rows) {
            System.out.println("Row Data: " + Arrays.toString(row));
        }

        List<BoardRequestDTO> boards = new ArrayList<>();
            for(Object[] row : rows) {
                BoardRequestDTO boardRequest = new BoardRequestDTO();
                boardRequest.setMainCategory((String)row[0]);
                boardRequest.setSubCategory((String) row[1]);
                boardRequest.setCategoryBoardNo(((Number)row[2]).intValue());
                boardRequest.setBoardNo(((Number)row[3]).intValue());
                boardRequest.setTitle((String) row[4]);
                boardRequest.setNickname((String) row[5]);
                boardRequest.setContent((String) row[6]);
                boardRequest.setCreateDate(((Timestamp) row[7]).toLocalDateTime());
                boardRequest.setViewCount(((Number) row[8]).intValue());

                boards.add(boardRequest);
        }
        boards.sort(Comparator.comparing(BoardRequestDTO::getCreateDate).reversed());
        return boards;
    }
}