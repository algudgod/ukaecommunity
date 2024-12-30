package com.community.ukae.repository.board;

import com.community.ukae.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    List<Board> findByMainCategoryAndSubCategory(String mainCategory, String subCategory);

}
