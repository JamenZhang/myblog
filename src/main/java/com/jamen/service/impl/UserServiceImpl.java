package com.jamen.service.impl;

import com.jamen.constant.RoleConstant;
import com.jamen.mapper.UserMapper;
import com.jamen.model.User;
import com.jamen.service.UserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: jamen
 * @Date: 2018/12/4 15:56
 * Describe: user表接口具体业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User findUserByPhone(String phone) {
        return userMapper.findUserByPhone(phone);
    }

    @Override
    public String findUsernameById(int id) {
        return userMapper.findUsernameById(id);
    }

    @Override
    public String insert(User user) {

        user.setUsername(user.getUsername().trim().replaceAll(" ", ""));
        String username = user.getUsername();

        if(username.length() > 35 || "".equals(username)){
            return "4";
        }
        if(userIsExist(user.getPhone())){
            return "1";
        }
        if("male".equals(user.getGender())){
            user.setAvatarImgUrl("https://jamen-myblog.oss-cn-shenzhen.aliyuncs.com/public/user/avatar/noLogin_male.jpg");
        } else {
            user.setAvatarImgUrl("https://jamen-myblog.oss-cn-shenzhen.aliyuncs.com/public/user/avatar/noLogin_female.jpg");
        }
        userMapper.insert(user);
        int userId = userMapper.findUserIdByPhone(user.getPhone());
        insertRole(userId, RoleConstant.ROLE_USER);
        return "2";
    }

    @Override
    public int findUserIdByPhone(String phone) {
        return 0;
    }

    @Override
    public void updatePasswordByPhone(String phone, String password) {
        userMapper.updatePassword(phone, password);
//        密码修改成功后注销当前用户
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    public String findPhoneByUsername(String username) {
        return userMapper.findPhoneByUsername(username);
    }

    @Override
    public int findIdByUsername(String username) {
        return userMapper.findIdByUsername(username);
    }

    @Override
    public User findUsernameByPhone(String phone) {
        return userMapper.findUsernameByPhone(phone);
    }

    @Override
    public void updateRecentlyLanded(String username, String recentlyLanded) {
        String phone = userMapper.findPhoneByUsername(username);
        userMapper.updateRecentlyLanded(phone, recentlyLanded);
    }

    @Override
    public boolean usernameIsExist(String username) {
        User user = userMapper.findUsernameByUsername(username);
        return user != null;
    }

    @Override
    public boolean isSuperAdmin(String phone) {
        int userId = userMapper.findUserIdByPhone(phone);
        List<Object> roleIds = userMapper.findRoleIdByUserId(userId);

        for(Object i : roleIds){
            if((int)i == 3){
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateAvatarImgUrlById(String avatarImgUrl, int id) {
        userMapper.updateAvatarImgUrlById(avatarImgUrl, id);
    }

    @Override
    public JSONObject getHeadPortraitUrl(int id) {
        JSONObject jsonObject = new JSONObject();
        String avatarImgUrl = userMapper.getHeadPortraitUrl(id);
        if(!"".equals(avatarImgUrl) && avatarImgUrl != null){
            jsonObject.put("status",200);
            jsonObject.put("avatarImgUrl",avatarImgUrl);
        }
        return jsonObject;
    }

    @Override
    public JSONObject getUserPersonalInfoByUsername(String username) {
        User user = userMapper.getUserPersonalInfoByUsername(username);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",200);
        JSONObject userJon = new JSONObject();
        userJon.put("phone",user.getPhone());
        userJon.put("username",user.getUsername());
        userJon.put("gender",user.getGender());
        userJon.put("trueName",user.getTrueName());
        userJon.put("birthday",user.getBirthday());
        userJon.put("email",user.getEmail());
        userJon.put("personalBrief",user.getPersonalBrief());
        userJon.put("avatarImgUrl",user.getAvatarImgUrl());
        jsonObject.put("result",userJon);
        return jsonObject;
    }

    @Override
    public JSONObject savePersonalDate(User user, String username) {
        JSONObject returnJson = new JSONObject();

        user.setUsername(user.getUsername().trim().replaceAll(" ",""));
        String newName = user.getUsername();
        if(newName.length() > 35){
            returnJson.put("status",501);
            return returnJson;
        } else if ("".equals(newName)){
            returnJson.put("status",502);
            return returnJson;
        }

        //改了昵称
        if(!newName.equals(username)){
            if(usernameIsExist(newName)){
                returnJson.put("status",500);
                return returnJson;
            }
            returnJson.put("status",200);
            //注销当前登录用户
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        //没改昵称
        else {
            returnJson.put("status",201);
        }
        userMapper.savePersonalDate(user, username);

        return returnJson;
    }

    @Override
    public String getHeadPortraitUrlByUserId(int userId) {
        return userMapper.getHeadPortraitUrl(userId);
    }

    @Override
    public int countUserNum() {
        return userMapper.countUserNum();
    }

    /**
     * 增加用户权限
     * @param userId 用户id
     * @param roleId 权限id
     */
    private void insertRole(int userId, int roleId) {
        userMapper.insertRole(userId, roleId);
    }

    /**
     * 通过手机号判断用户是否存在
     * @param phone 手机号
     * @return true--存在  false--不存在
     */
    private boolean userIsExist(String phone){
        User user = userMapper.findUserByPhone(phone);
        return user != null;
    }
}
