package com.example.board.dao;

import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;

//spring이 관리하는 bean
@Repository
public class UserDao {
    private  final NamedParameterJdbcTemplate jdbcTemplate;
    private  SimpleJdbcInsertOperations insertUser;

    public UserDao(DataSource dataSource){
        jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
        insertUser=new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");
    }

    //Spring JDBC를 이용한 코드
    @Transactional //두개의 쿼리가 사용되기 때문에 @Transactional 사용.
    // service에서 트랜잭션이 시작되었으므로 그 트랜잭션안에 포함된다.
    public User addUser(String email, String name, String password){
        //insert into user(email,name,password,regdate) values (?,?,?,now());
        //select last_insert_id();
        User user=new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRegdate(LocalDateTime.now());
        user.setName(name);
        SqlParameterSource params=new BeanPropertySqlParameterSource(user);
        Number num=insertUser.executeAndReturnKey(params);//insert를 실행하고,자동으로 생성된 id를 가져온다.
        int userId=num.intValue();
        user.setUserId(userId);


        return user;
    }
    @Transactional
    public void mappingUserRole(int userId){
        //insert into user_role(user_id,role_id) values (?,1);
        String sql="insert into user_role(user_id,role_id) values (:userId,1)";
        SqlParameterSource params=new MapSqlParameterSource("userId",userId);
        jdbcTemplate.update(sql,params);
    }
    @Transactional
    public User getUser(String email){
        try {
            String sql = "select user_id,email,name,password,regdate from user where email=:email";
            SqlParameterSource params = new MapSqlParameterSource("email", email);
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            //user_id=>setUserId,email=>setEmail
            User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
            return user;
        }catch (Exception e){
            return null;
        }
    }
}
