package com.jamen.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jamen.component.JavaScriptCheck;
import com.jamen.constant.SiteOwner;
import com.jamen.mapper.LeaveMessageMapper;
import com.jamen.model.LeaveMessage;
import com.jamen.model.UserReadNews;
import com.jamen.redis.HashRedisServiceImpl;
import com.jamen.service.LeaveMessageLikesRecordService;
import com.jamen.service.LeaveMessageService;
import com.jamen.service.UserService;
import com.jamen.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: jamen
 * @Date: 2018/12/15 14:01
 * Describe:
 */
@Service
public class LeaveMessageServiceImpl implements LeaveMessageService {

    @Autowired
    LeaveMessageMapper leaveMessageMapper;
    @Autowired
    LeaveMessageLikesRecordService leaveMessageLikesRecordService;
    @Autowired
    UserService userService;
    @Autowired
    HashRedisServiceImpl hashRedisServiceImpl;

    @Override
    public void publishLeaveMessage(String leaveMessageContent, String pageName, String answerer) {

        TimeUtil timeUtil = new TimeUtil();
        String nowStr = timeUtil.getFormatDateForFive();
        leaveMessageContent = JavaScriptCheck.javaScriptCheck(leaveMessageContent);
        LeaveMessage leaveMessage = new LeaveMessage(pageName, userService.findIdByUsername(answerer), userService.findIdByUsername(SiteOwner.SITE_OWNER), nowStr, leaveMessageContent);

        if(leaveMessage.getAnswererId() == leaveMessage.getRespondentId()){
            leaveMessage.setIsRead(0);
        }
        leaveMessageMapper.publishLeaveMessage(leaveMessage);
        //redis中保存该用户未读消息
        addNotReadNews(leaveMessage);
    }

    @Override
    public LeaveMessage publishLeaveMessageReply(LeaveMessage leaveMessage, String respondent) {
        TimeUtil timeUtil = new TimeUtil();
        String nowStr = timeUtil.getFormatDateForFive();
        leaveMessage.setLeaveMessageDate(nowStr);
        leaveMessage.setRespondentId(userService.findIdByUsername(respondent));
        if(leaveMessage.getAnswererId() == leaveMessage.getRespondentId()){
            leaveMessage.setIsRead(0);
        }
        leaveMessageMapper.publishLeaveMessage(leaveMessage);
        //redis中保存该用户未读消息
        addNotReadNews(leaveMessage);
        return leaveMessage;
    }

    @Override
    public JSONObject leaveMessageNewReply(LeaveMessage leaveMessage, String answerer, String respondent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",200);
        JSONObject result = new JSONObject();
        result.put("answerer",answerer);
        result.put("respondent",respondent);
        result.put("leaveMessageContent",leaveMessage.getLeaveMessageContent());
        result.put("leaveMessageDate",leaveMessage.getLeaveMessageDate());
        jsonObject.put("result",result);
        return jsonObject;
    }

    @Override
    public JSONObject findAllLeaveMessage(String pageName, int pId, String username) {

        List<LeaveMessage> leaveMessages = leaveMessageMapper.findAllLeaveMessage(pageName, pId);
        JSONObject returnJson,replyJson;
        JSONObject leaveMessageJson = new JSONObject();
        JSONArray replyJsonArray;
        JSONArray leaveMessageJsonArray = new JSONArray();
        List<LeaveMessage> leaveMessageReplies;

        returnJson = new JSONObject();
        returnJson.put("status",200);

        for(LeaveMessage leaveMessage : leaveMessages){
            leaveMessageJson = new JSONObject();
            leaveMessageJson.put("id",leaveMessage.getId());
            leaveMessageJson.put("answerer",userService.findUsernameById(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageDate",leaveMessage.getLeaveMessageDate());
            leaveMessageJson.put("likes",leaveMessage.getLikes());
            leaveMessageJson.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageContent",leaveMessage.getLeaveMessageContent());
            if(null == username){
                leaveMessageJson.put("isLiked",0);
            } else {
                if(!leaveMessageLikesRecordService.isLiked(pageName, leaveMessage.getId(), userService.findIdByUsername(username))){
                    leaveMessageJson.put("isLiked",0);
                } else {
                    leaveMessageJson.put("isLiked",1);
                }
            }

            leaveMessageReplies = leaveMessageMapper.findLeaveMessageReplyByPageNameAndPid(pageName, leaveMessage.getId());
            replyJsonArray = new JSONArray();
            for(LeaveMessage reply : leaveMessageReplies){
                replyJson = new JSONObject();
                replyJson.put("id", reply.getId());
                replyJson.put("answerer", userService.findUsernameById(reply.getAnswererId()));
                replyJson.put("respondent", userService.findUsernameById(reply.getRespondentId()));
                replyJson.put("leaveMessageDate", reply.getLeaveMessageDate());
                replyJson.put("leaveMessageContent", reply.getLeaveMessageContent());

                replyJsonArray.add(replyJson);
            }
            leaveMessageJson.put("replies",replyJsonArray);
            leaveMessageJsonArray.add(leaveMessageJson);
        }
        returnJson.put("result",leaveMessageJsonArray);

        return returnJson;
    }

    @Override
    public int updateLikeByPageNameAndId(String pageName, int id) {
        leaveMessageMapper.updateLikeByPageNameAndId(pageName, id);
        return leaveMessageMapper.findLikesByPageNameAndId(pageName, id);
    }

    @Override
    public JSONObject getUserLeaveMessage(int rows, int pageNum, String username) {

        int respondentId = userService.findIdByUsername(username);
        PageHelper.startPage(pageNum, rows);
        List<LeaveMessage> leaveMessages = leaveMessageMapper.getUserLeaveMessage(respondentId);
        PageInfo<LeaveMessage> pageInfo = new PageInfo<>(leaveMessages);
        JSONObject returnJson = new JSONObject();
        returnJson.put("status",200);
        JSONObject leaveMessageJson;
        JSONArray leaveMessageJsonArray = new JSONArray();
        for(LeaveMessage leaveMessage : leaveMessages){
            leaveMessageJson = new JSONObject();
            leaveMessageJson.put("id",leaveMessage.getId());
            leaveMessageJson.put("pId",leaveMessage.getPId());
            leaveMessageJson.put("pageName",leaveMessage.getPageName());
            leaveMessageJson.put("answerer",userService.findUsernameById(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageDate",leaveMessage.getLeaveMessageDate());
            leaveMessageJson.put("isRead",leaveMessage.getIsRead());
            leaveMessageJsonArray.add(leaveMessageJson);
        }

        returnJson.put("result",leaveMessageJsonArray);
        returnJson.put("msgIsNotReadNum",leaveMessageMapper.countIsReadNumByRespondentId(respondentId));

        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNum",pageInfo.getPageNum());
        pageJson.put("pageSize",pageInfo.getPageSize());
        pageJson.put("total",pageInfo.getTotal());
        pageJson.put("pages",pageInfo.getPages());
        pageJson.put("isFirstPage",pageInfo.isIsFirstPage());
        pageJson.put("isLastPage",pageInfo.isIsLastPage());

        returnJson.put("pageInfo",pageJson);
        return returnJson;
    }

    @Override
    public JSONObject findFiveNewComment(int rows, int pageNum) {
        JSONObject returnJson = new JSONObject();
        PageHelper.startPage(pageNum, rows);
        List<LeaveMessage> fiveLeaveWords = leaveMessageMapper.findFiveNewLeaveWord();
        PageInfo<LeaveMessage> pageInfo = new PageInfo<>(fiveLeaveWords);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for(LeaveMessage leaveMessage : fiveLeaveWords){
            jsonObject = new JSONObject();
            if(leaveMessage.getPId() != 0){
                leaveMessage.setLeaveMessageContent("@" + userService.findUsernameById(leaveMessage.getRespondentId()) + " " + leaveMessage.getLeaveMessageContent());
            }
            jsonObject.put("id",leaveMessage.getId());
            jsonObject.put("pagePath",leaveMessage.getPageName());
            jsonObject.put("answerer",userService.findUsernameById(leaveMessage.getAnswererId()));
            jsonObject.put("leaveWordDate",leaveMessage.getLeaveMessageDate().substring(0,10));
            jsonObject.put("leaveWordContent",leaveMessage.getLeaveMessageContent());
            jsonArray.add(jsonObject);
        }

        returnJson.put("status",200);
        returnJson.put("result",jsonArray);
        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNum",pageInfo.getPageNum());
        pageJson.put("pageSize",pageInfo.getPageSize());
        pageJson.put("total",pageInfo.getTotal());
        pageJson.put("pages",pageInfo.getPages());
        pageJson.put("isFirstPage",pageInfo.isIsFirstPage());
        pageJson.put("isLastPage",pageInfo.isIsLastPage());
        returnJson.put("pageInfo",pageJson);
        return returnJson;
    }

    @Override
    public int countLeaveMessageNum() {
        return leaveMessageMapper.countLeaveMessageNum();
    }

    @Override
    public int readOneLeaveMessageRecord(int id) {
        try {
            leaveMessageMapper.readOneLeaveMessageRecord(id);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public JSONObject readAllLeaveMessage(String username) {
        int respondentId = userService.findIdByUsername(username);
        leaveMessageMapper.readLeaveMessageRecordByRespondentId(respondentId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",200);
        jsonObject.put("result","success");
        return jsonObject;
    }

    /**
     * 保存评论成功后往redis中增加一条未读评论数
     */
    private void addNotReadNews(LeaveMessage leaveMessage){
        if(leaveMessage.getRespondentId() != leaveMessage.getAnswererId()){
            Boolean isExistKey = hashRedisServiceImpl.hasKey(leaveMessage.getRespondentId()+"");
            if(!isExistKey){
                UserReadNews news = new UserReadNews(1,0,1);
                hashRedisServiceImpl.put(String.valueOf(leaveMessage.getRespondentId()), news, UserReadNews.class);
            } else {
                hashRedisServiceImpl.hashIncrement(leaveMessage.getRespondentId()+"", "allNewsNum",1);
                hashRedisServiceImpl.hashIncrement(leaveMessage.getRespondentId()+"", "leaveMessageNum",1);
            }
        }
    }
}
