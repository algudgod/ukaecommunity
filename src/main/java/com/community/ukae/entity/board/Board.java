package com.community.ukae.entity.board;

import com.community.ukae.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardNo;
    private String category;
    private String tag;
    private String title;
    private String content;
    @Column(updatable = false, insertable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    @ManyToOne
    @JoinColumn(name = "login_id", nullable = false)
    private User user;
    private int viewCount; // default: 0

}
