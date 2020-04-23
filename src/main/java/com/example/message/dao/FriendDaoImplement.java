package com.example.message.dao;

import com.example.message.entity.UserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendDaoImplement implements FriendDao{
    private JdbcTemplate jdbcTemplate;

    //为什么要写这个构造函数呢？是为了Bean的注入吗？
    public FriendDaoImplement(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String findFriendByUid(String friend_uid) {
        List<UserEntity> userInfo = jdbcTemplate.query("SELECT username FROM user WHERE uid = ?",
                (resultSet, i) -> {
                    UserEntity user = new UserEntity();
                    user.setUsername(resultSet.getString("username"));
                    return user;
                }, friend_uid);//query方法的参数为sql语句，接收返回值的对象，查询用的变量，此处的lambda函数和uid对应后两个
        if(userInfo.isEmpty()){
            return "null";//查无此人 query返回什么？ query什么都不返回，编译器似乎默认一个null出来（注意：不是"null"）
        }
        UserEntity user = userInfo.get(0);//这里不check非空的话会报错，需要先check是否为空！！！
        return user.getUsername();
    }

    @Override
    public int insertFriend(String uid, String friend_uid) {
        List<UserEntity> userInfo = jdbcTemplate.query("SELECT friend_uid_list FROM user WHERE uid = ?",
                (resultSet, i) -> {
                    UserEntity user = new UserEntity();
                    user.setFriendUidList(resultSet.getString("friend_uid_list"));
                    return user;
                }, uid);//查询自己的friend_uid_list
        UserEntity user = userInfo.get(0);
        String friend_uid_list = user.getFriendUidList();
        if(friend_uid_list.equals("null")){//如果为空
            friend_uid_list = friend_uid;
        }
        else{
            friend_uid_list += friend_uid;
        }
        return jdbcTemplate.update("UPDATE user SET friend_uid_list = ? WHERE (uid = ?)",friend_uid_list, uid);
    }
}