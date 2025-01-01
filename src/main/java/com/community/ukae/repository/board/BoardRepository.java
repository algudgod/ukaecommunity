package com.community.ukae.repository.board;

import com.community.ukae.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    List<Board> findByMainCategoryAndSubCategory(String mainCategory, String subCategory);

    @Query(value = "SELECT " +
            "b.main_category, " +
            "b.sub_category, " +
            "ROW_NUMBER() OVER (PARTITION BY b.main_category, b.sub_category ORDER BY b.board_no) AS categoryBoardNo, " +
            "b.board_no, " +
            "b.title, " +
            "u.nickname, " +
            "b.content, " +
            "b.create_date, " +
            "b.view_count " +
            "FROM board b " +
            "JOIN user u ON b.login_id = u.login_id " +
            "WHERE b.main_category = :mainCategory " +
            "AND b.sub_category = :subCategory",
            nativeQuery = true)
    List<Object[]> findByCategoryWithRowNumber(@Param("mainCategory") String mainCategory,
                                               @Param("subCategory") String subCategory);
}