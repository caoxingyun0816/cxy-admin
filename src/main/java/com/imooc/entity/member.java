package com.imooc.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by caoxingyun on 2018/7/23.
 * 会员表
 */
@Entity
public class member {

    @Id
    @GeneratedValue
    private Integer usersId;

    private String userName;

    private String userSex;

    private String userGrade;

    private String userEndTime;

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserEndTime() {
        return userEndTime;
    }

    public void setUserEndTime(String userEndTime) {
        this.userEndTime = userEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        member member = (member) o;

        if (!usersId.equals(member.usersId)) return false;
        if (!userName.equals(member.userName)) return false;
        if (!userSex.equals(member.userSex)) return false;
        if (!userGrade.equals(member.userGrade)) return false;
        return userEndTime.equals(member.userEndTime);
    }

    @Override
    public int hashCode() {
        int result = usersId.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + userSex.hashCode();
        result = 31 * result + userGrade.hashCode();
        result = 31 * result + userEndTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "member{" +
                "usersId=" + usersId +
                ", userName='" + userName + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userGrade='" + userGrade + '\'' +
                ", userEndTime='" + userEndTime + '\'' +
                '}';
    }
}
