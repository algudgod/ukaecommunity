package com.community.ukae.repository.board;

import com.community.ukae.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 조회
    List<Board> findByMainCategoryAndSubCategory(String mainCategory, String subCategory);
    Optional<Board> findByBoardNo(int boardNo);

    int countByMainCategoryAndSubCategory(String mainCategory, String subCategory);

    @Query(value = "SELECT " +
            "b.main_category, " +
            "b.sub_category, " +
            "ROW_NUMBER() OVER (PARTITION BY b.main_category, b.sub_category ORDER BY b.board_no) AS categoryBoardNo, " +
            "b.board_no, " +
            "b.title, " +
            "u.nickname, " +
            "b.content, " +
            "b.create_date, " +
            "b.view_count, " +
            "b.tag "+
            "FROM board b " +
            "JOIN user u ON b.login_id = u.login_id " +
            "WHERE b.main_category = :mainCategory " +
            "AND b.sub_category = :subCategory " +
            "ORDER BY b.create_date DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Object[]> findByCategoryWithPagination(@Param("mainCategory") String mainCategory,
                                               @Param("subCategory") String subCategory,
                                               @Param("limit") int limit,
                                               @Param("offset") int offset);
}