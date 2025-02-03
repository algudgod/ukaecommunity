package com.community.ukae.entity.imageFile;

import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.comment.Comment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK & 자동 증가
    private int imageNo;

    @ManyToOne
    @JoinColumn(name = "board_no", nullable = false) //FK 설정
    private Board board;
    @ManyToOne
    @JoinColumn(name= "comment_no", nullable= false)
    private Comment comment;

    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "create_date", updatable = false, insertable = false)
    private LocalDateTime createDate;

}
