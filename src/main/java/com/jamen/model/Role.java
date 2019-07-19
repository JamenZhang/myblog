package com.zhy.model;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: jamen
 * @Date: 2018/12/5 19:41
 * Describe: 权限
 */
@Data
@NoArgsConstructor
public class Role {

    private int id;

    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
