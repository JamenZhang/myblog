
package com.zhy.controller;

import com.zhy.component.JavaScriptCheck;
import com.zhy.model.LeaveMessage;
import com.zhy.model.LeaveMessageLikesRecord;
import com.zhy.service.LeaveMessageLikesRecordService;
import com.zhy.service.LeaveMessageService;
import com.zhy.service.UserService;
import com.zhy.utils.TimeUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author: jamen
 * @Date: 2018/12/15 13:55
 * Describe:
 */
@RestController
public class LeaveMessageControl {

    private Logger logger = LoggerFactory.getLogger(LeaveMessageControl.class);

    @Autowired
    LeaveMessageService leaveMessageService;
    @Autowired
    LeaveMessageLikesRecordService leaveMessageLikesRecordService;
    @Autowired
    UserService userService;

    /**
     * 发表留言
     * @param leaveMessageContent 留言内容
     * @param pageName 留言页
     * @param principal 当前用户
     * @return
     */
    @PostMapping("/publishLeaveMessage")
    public JSONObject publishLeaveMessage(@RequestParam("leaveMessageContent") String leaveMessageContent,
                                          @RequestParam("pageName") String pageName,
                                          @AuthenticationPrincipal Principal principal){

        String answerer = null;
        JSONObject jsonObject;
        try {
            answerer = principal.getName();
        } catch (NullPointerException e){
            logger.info("This user is not login-发表留言");
            jsonObject = new JSONObject();
            jsonObject.put("status",403);
            jsonObject.put("result","You are not sign in");
            return jsonObject;
        }
        leaveMessageService.publishLeaveMessage(leaveMessageContent,pageName, answerer);
        return leaveMessageService.findAllLeaveMessage(pageName, 0, answerer);

    }

    /**
     * 获得当前页的留言
     * @param pageName 当前页
     * @return
     */
    @GetMapping("/getPageLeaveMessage")
    public JSONObject getPageLeaveMessage(@RequestParam("pageName") String pageName,
                                          @AuthenticationPrincipal Principal principal){
        String username = null;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            logger.info("This user is not login—留言");
        }
        return leaveMessageService.findAllLeaveMessage(pageName, 0, username);
    }

    /**
     * 发布留言中的评论
     * @return
     */
    @PostMapping("/publishLeaveMessageReply")
    public JSONObject publishLeaveMessageReply(LeaveMessage leaveMessage,
                                               @RequestParam("parentId") String parentId,
                                               @RequestParam("respondent") String respondent,
                                               @AuthenticationPrincipal Principal principal){
        String username = null;
        JSONObject jsonObject;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            jsonObject = new JSONObject();
            jsonObject.put("status",403);
            jsonObject.put("result","You are not sign in");
        }
        leaveMessage.setAnswererId(userService.findIdByUsername(username));
        leaveMessage.setPId(Integer.parseInt(parentId.substring(1)));
        leaveMessage.setLeaveMessageContent(JavaScriptCheck.javaScriptCheck(leaveMessage.getLeaveMessageContent()));
        String commentContent = leaveMessage.getLeaveMessageContent();
        if('@' == commentContent.charAt(0)){
            leaveMessage.setLeaveMessageContent(commentContent.substring(respondent.length() + 1).trim());
        } else {
            leaveMessage.setLeaveMessageContent(commentContent.trim());
        }
        if("".equals(leaveMessage.getLeaveMessageContent())){
            jsonObject = new JSONObject();
            jsonObject.put("status",400);
            return jsonObject;
        }
        leaveMessage = leaveMessageService.publishLeaveMessageReply(leaveMessage, respondent);

        return leaveMessageService.leaveMessageNewReply(leaveMessage, username, respondent);
    }

    /**
     * 点赞
     * @return 点赞数
     */
    @GetMapping("/addLeaveMessageLike")
    public int addLeaveMessageLike(@RequestParam("pageName") String pageName,
                                   @RequestParam("respondentId") String respondentId,
                                   @AuthenticationPrincipal Principal principal){

        String username;
        try {
            username = principal.getName();
        } catch (NullPointerException e){
            logger.info("This user is not login-点赞");
            return -1;
        }
        TimeUtil timeUtil = new TimeUtil();
        int userId = userService.findIdByUsername(username);
        LeaveMessageLikesRecord leaveMessageLikesRecord = new LeaveMessageLikesRecord(pageName, Integer.parseInt(respondentId.substring(1)), userId, timeUtil.getFormatDateForFive());
        if(leaveMessageLikesRecordService.isLiked(leaveMessageLikesRecord.getPageName(), leaveMessageLikesRecord.getPId(), userId)){
            logger.info("This user had clicked good for this page");
            return -2;
        }
        int likes = leaveMessageService.updateLikeByPageNameAndId(pageName, leaveMessageLikesRecord.getPId());
        leaveMessageLikesRecordService.insertLeaveMessageLikesRecord(leaveMessageLikesRecord);
        return likes;
    }

}
