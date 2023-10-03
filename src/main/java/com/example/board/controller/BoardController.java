package com.example.board.controller;

import com.example.board.dto.Board;
import com.example.board.dto.LoginInfo;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
//브라우저에 요청(HTTP)을 받아서 응답해주는 객체.
//@Component->스프링 부트가 자동으로 Bean 생성.
public class BoardController {

    private final BoardService boardService;

    //게시물 목록 보여준다.
    //컨트롤러 메소드가 리턴하는 문자열은 템플릿 이름이다.
    //http://localhost:8080/ -->"list라는 이름의 템플릿을 사용하여 화면에 출력.(forward)"
    //list를 리턴한다는 뜻
    //ThymeleafProperties속성에 의해 classpathL/templates.list.html 경로로 파일 만들어줘야 한다.

    @GetMapping("/")
    public String list(@RequestParam(name="page",defaultValue = "1") int page,HttpSession session, Model model){//HttpSession,Model은 Spring이 자동으로 넣어준다. Model은 템블릿에 값을 넘기기위함
        //게시물 목록 읽어오기
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        model.addAttribute("loginInfo",loginInfo);//템블릿에게 객체를 넘긴다.
        //페이징 처리
        int totalCount=boardService.getTotalCount();
        List<Board> list=boardService.getBoards(page);// page가 1,2,3,4..
        int pageCount=totalCount/10;
        if(totalCount%10>0){
            pageCount++;
        }
        int currentPage=page;
//        System.out.println(totalCount);
//        for(Board board:list){
//            System.out.println(board);
//        }
        model.addAttribute("list",list);
        model.addAttribute("pageCount",pageCount);
        model.addAttribute("currentPage",page);


        return "list";
    }

    //board?id=숫자 -> 파라미터 id,파라미터 id 값
    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId,Model model){
        System.out.println(boardId);

        //id에 해당하는 게시물을 읽어들인다.
        //id에 해당하는 게시물의 조회수도 1증가.
        Board board= boardService.getBoard(boardId);
        model.addAttribute("board",board);
        return "board";
    }

    //삭제한다. 관리자는 모든 글을 삭제할 수 있다.
    //수정한다. 자신이 작성한 글.관리자가 수정은 불가.
    @GetMapping("/writeform")
    public String writeForm(HttpSession session,Model model){
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        if(loginInfo ==null){//세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo",loginInfo);
        return "writeForm";
    }

    @PostMapping("/write")
    public String post(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    ){
        //로그인한 사용자만 글쓰기 가능. 하지 않았다면 리스트보기로 자동 이동.
        //세션에서 로그인한 정보를 읽어들인다.
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        if(loginInfo ==null){//세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }
        //로그인한 회원정보+ 제목, 내용을 저장한다.
        System.out.println(title);
        System.out.println(content);

        boardService.addBoard(loginInfo.getUserId(),title,content);

        return "redirect:/";// 리스트보기로 리다이렉트한다.
    }
    @GetMapping("/delete")
    public  String delete(@RequestParam("boardId") int boardId,HttpSession session){
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        if(loginInfo==null){
            return "redirect:/loginform";
        }

        List<String> roles=loginInfo.getRoles();
        //권한이 있으면 삭제가능
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else{
            boardService.deleteBoard(loginInfo.getUserId(),boardId);
        }
        return "redirect:/";
    }
    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId")int boardId,Model model,HttpSession session){
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        if(loginInfo ==null){//세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }
        //boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        Board board=boardService.getBoard(boardId,false);//수정 시 조회수 증가 방지
        model.addAttribute("board",board);
        model.addAttribute("loginInfo",loginInfo);
        return "updateform";
    }
    @PostMapping("/update")
    public String update(
            @RequestParam("boardId") int boardId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    ){
        LoginInfo loginInfo=(LoginInfo) session.getAttribute("loginInfo");
        if(loginInfo ==null){//세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }
        //boardId에 해당하는 글의 제목과 내용을 수정한다.
        //글쓴이만 수정가능
        Board board=boardService.getBoard(boardId,false);
        if(board.getUserId() !=loginInfo.getUserId()){
            return "redirect:/board?boardId="+boardId; //글보기로 이동
        }
        boardService.updateBoard(boardId,title,content);
        return "redirect:/board?boardId="+boardId; //수정된 글로 리다이렉트.
    }
}
