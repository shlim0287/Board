package com.example.board.dao;

import com.example.board.dto.Board;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository

public class BoardDao {
    private  final NamedParameterJdbcTemplate jdbcTemplate;
    private  final SimpleJdbcInsertOperations insertBoard;

    //생성자 주입. 스프링이 자동으로 Hikari cp 빈을 주입
    public BoardDao(DataSource dataSource){
        jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
        insertBoard=new SimpleJdbcInsert(dataSource)
                .withTableName("board")
                .usingGeneratedKeyColumns("board_id");//자동으로 증가되는 id 설정
    }
    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board=new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDate.now());
        SqlParameterSource params= new BeanPropertySqlParameterSource(board);
        insertBoard.execute(params);

    }
    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql="select count(*) as total_count from board";
        Integer totalCount=jdbcTemplate.queryForObject(sql, Map.of(),Integer.class);
        System.out.println(totalCount);
        return totalCount.intValue();

    }
    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        //start는 0,10,20,30 ->1page~4page
        int start=(page-1)*10;
        String sql="select b.user_id,b.board_id,b.title,b.regdate,b.view_cnt,u.name from board b,user u where b.user_id=u.user_id order by board_id desc limit :start,10";
        RowMapper<Board> rowMapper= BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> list=jdbcTemplate.query(sql,Map.of("start",start),rowMapper);

        return list;
    }
    @Transactional(readOnly = true)
    public Board getBoard(int boardId) {
        String sql="select b.user_id,b.board_id,b.title,b.regdate,b.view_cnt,u.name,b.content from board b,user u where b.user_id=u.user_id and b.board_id=:boardId";
        RowMapper<Board> rowMapper=BeanPropertyRowMapper.newInstance(Board.class);
        Board board=jdbcTemplate.queryForObject(sql,Map.of("boardId",boardId),rowMapper);
        return board;
    }
    @Transactional
    public void updateViewCnt(int boardId) {
        String sql="update board\n"+"set view_cnt=view_cnt +1\n"+"where board_id=:boardId";
        jdbcTemplate.update(sql,Map.of("boardId",boardId));
    }
@Transactional
    public void deleteBoard(int boardId) {
        String sql="delete from board where board_id=:boardId";
        jdbcTemplate.update(sql,Map.of("boardId",boardId));
    }

    public void updateBoard(int boardId, String title, String content) {
        String sql="update board set title=:title,content=:content where board_id=:boardId ";
        Board board=new Board();
        board.setBoardId(boardId);
        board.setContent(content);
        board.setTitle(title);
        SqlParameterSource params=new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql,params);
    }
}
