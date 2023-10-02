package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class Board {
    private int boardId;
    private String title;
    private String content;
    private int userId;
    private String name;//join한 컬럼을 위해.
    private LocalDate regdate;
    private int view_cnt;
}
