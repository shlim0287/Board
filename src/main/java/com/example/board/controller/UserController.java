package com.example.board.controller;

import com.example.board.dto.LoginInfo;
import com.example.board.dto.User;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //http://localhost:8080/userRegForm
    @GetMapping("/userRegForm")
    public String userRegForm(){
        return "userRegForm";
    }

    /**
     * 회원정보 등록
     * @param name
     * @param email
     * @param password
     * @return
     */
    //서버가 post로 전달된 값 처리
    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password


            //회원정보 저장(service)
    ){
        // /welcome으로 리다이렉트
        System.out.println(name);
        System.out.println(email);
        System.out.println(password);

        userService.addUser(name,email,password);

        return "redirect:/welcome"; //브라우저에게 자동으로 http://localhost:8080/welcome으로 이동
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/loginform")
    public String loginform(){
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession //spring이 자동으로 session을 처리하는 객체를 넣어준다.
    ){
       //email에 해당하는 회원정보를 읽어들인 후 (service)
       // 아이디 암호가 맞다면 세션에서 회원정보 저장.
        System.out.println(email);
        System.out.println(password);

        try{
            User user=userService.getUser(email);
            if(user.getPassword().equals(password)){
                System.out.println("암호가 같음");
                LoginInfo loginInfo=new LoginInfo(user.getUserId(),user.getEmail(),user.getName());

                //권한정보 읽어들여 loginInfo에 추가
                List<String> roles=userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);

                httpSession.setAttribute("loginInfo",loginInfo);//첫번째 파라미터가 key,두번째 파라미터가 값.
                System.out.println("세션에 로그인 정보 저장");
            }else {
                throw new RuntimeException("암호가 같지 않음");
            }
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:/loginform?error=true"; //email에 해당하는 정보 없으면 로그인 폼으로 이동
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        //세션에서 회원정보 삭제.
        session.removeAttribute("loginInfo");
        return "redirect:/";
    }
}
