package com.community.ukae.entity.imageFile;

import com.community.ukae.entity.board.Board;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK & 자동 증가
    private int imageNo;
    @ManyToOne
    @JoinColumn(name = "board_no", nullable = false) //FK 설정
    private Board board;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "create_date", updatable = false, insertable = false)
    private LocalDateTime createDate;

}
