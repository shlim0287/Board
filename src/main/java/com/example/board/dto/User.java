package com.example.board.dto;

import com.example.board.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor// 기본 생성자
@ToString//Object의 toString()를 자동 생성
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime regdate;// 날짜 type으로 읽어온 후 문자열로 변환하기!

}
