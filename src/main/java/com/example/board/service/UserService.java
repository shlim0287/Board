package com.example.board.service;

import com.example.board.dao.UserDao;
import com.example.board.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//트랜젝션 단위로 실행될 메소드를 선언하고 있는 인터페이스
//spring이 관리해주는 bean
//dao를 사용한다.
@Service
@RequiredArgsConstructor //lombok이 final 필드를 초기화하는 생성자를 자동으로 생성.
public class UserService {
    private final UserDao userDao;
    // spring이 UserService를 bean으로 생성할 때 생성자를 이용해 생성하는데, 이때 UserDao Bean이 있는지를 보고
    // 그 bean에 주입한다(생성자 주입).


    // service에서는 @Transactional을 붙여서 하나의 트랜젝션 단위로 처리.
    // springboot는 트랜젝션을 처리해주는 트랜젝션 관리자를 가지고있음(나중).
    @Transactional
    public User addUser(String name, String email, String password){
        //트랜잭션 시작
        User user1=userDao.getUser(email);//email중복검사
        if(user1!=null){
            throw new RuntimeException("이미 가입된 이메일입니다");
        }

        User user=userDao.addUser(email,name,password);
        userDao.mappingUserRole(user.getUserId());
        return user;
        //트랜잭션 종료
    }

    //회원정보 가져오는 기능 필요 -로그인 확인용
    @Transactional
    public User getUser(String email){
        return userDao.getUser(email);
    }
}
