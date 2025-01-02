package com.community.ukae.service.board;

import com.community.ukae.dto.board.BoardRequestDTO;
import com.community.ukae.dto.board.BoardResponseDTO;
import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.imageFile.ImageFile;
import com.community.ukae.entity.user.User;
import com.community.ukae.enums.BoardCategory;
import com.community.ukae.repository.board.BoardRepository;
import com.community.ukae.repository.imageFile.ImageFileRepository;
import com.community.ukae.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final BoardRepository boardRepository;
    private final ImageFileRepository imageFileRepository;
    private final S3Service s3Service;

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

    public void addBoard(BoardRequestDTO boardRequest, User user) throws IOException {

        validateAddBoardRequest(boardRequest);

        Board board = new Board();
        board.setMainCategory(boardRequest.getMainCategory());
        board.setSubCategory(boardRequest.getSubCategory());
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setUser(user);

        boardRepository.save(board);

    }

    private void validateAddBoardRequest(BoardRequestDTO boardRequest) {
        if (boardRequest.getTitle() == null || boardRequest.getTitle().length() < 3 || boardRequest.getTitle().length() > 100) {
            throw new IllegalArgumentException("제목은 3자 이상, 100자 이하이어야 합니다.");
        }
        if (boardRequest.getContent() == null || boardRequest.getContent().length() < 5 || boardRequest.getContent().length() > 2000) {
            throw new IllegalArgumentException("내용은 최소 5자 이상, 2000자 이하이어야 합니다.");
        }
    }

    // 특정 키테고리의 게시글 목록을 카테고리별 고유 번호와 함께 반환
    public List<BoardResponseDTO> getBoardWithCategoryNumbers(String mainCategory, String subCategory) {

        List<Object[]> rows = boardRepository.findByCategoryWithRowNumber(mainCategory, subCategory);

        for (Object[] row : rows) {
            System.out.println("Row Data: " + Arrays.toString(row));
        }

        List<BoardResponseDTO> boards = new ArrayList<>();
        for (Object[] row : rows) {
            BoardResponseDTO boardResponse = new BoardResponseDTO();
            boardResponse.setMainCategory((String) row[0]);
            boardResponse.setSubCategory((String) row[1]);
            boardResponse.setCategoryBoardNo(((Number) row[2]).intValue());
            boardResponse.setBoardNo(((Number) row[3]).intValue());
            boardResponse.setTitle((String) row[4]);
            boardResponse.setNickname((String) row[5]);
            boardResponse.setContent((String) row[6]);
            boardResponse.setCreateDate(((Timestamp) row[7]).toLocalDateTime());
            boardResponse.setViewCount(((Number) row[8]).intValue());

            boards.add(boardResponse);
        }
        boards.sort(Comparator.comparing(BoardResponseDTO::getCreateDate).reversed());
        return boards;
    }

    public BoardResponseDTO findBoardByBoardNo(int boardNo) {
        Board board = boardRepository.findByBoardNo(boardNo)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        // 이미지 URL 리스트 가져오기
        List<String> imageUrls = imageFileRepository.findByBoard_BoardNo(boardNo)
                .stream()
                .map(ImageFile::getImageUrl)
                .toList();

        return BoardResponseDTO.builder()
                .boardNo(board.getBoardNo())
                .mainCategory(board.getMainCategory())
                .subCategory(board.getSubCategory())
                .title(board.getTitle())
                .nickname(board.getUser().getNickname())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createDate(board.getCreateDate())
                .imageUrls(imageUrls) // 이미지 URL 리스트 설정
                .build();

    }


}