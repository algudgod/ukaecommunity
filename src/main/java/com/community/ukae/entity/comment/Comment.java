package com.community.ukae.entity.comment;

import com.community.ukae.entity.board.Board;
import com.community.ukae.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentNo;

    @ManyToOne
    @JoinColumn(name= "board_no", nullable= false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "login_id", nullable = false)
    private User user;

    private String content;

    @Column(updatable = false, insertable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

}
