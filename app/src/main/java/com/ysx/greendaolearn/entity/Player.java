package com.ysx.greendaolearn.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author ysx
 * @date 2017/10/31
 * @description
 */

@Entity
public class Player {
    @Id
    private Long id;
    @Unique
    private String name;
    private Integer age;
    @Generated(hash = 1461101279)
    public Player(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    @Generated(hash = 30709322)
    public Player() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return this.age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
