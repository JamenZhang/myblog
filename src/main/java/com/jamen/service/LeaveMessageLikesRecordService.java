package com.jamen.service;

import com.jamen.model.LeaveMessageLikesRecord;
import org.springframework.stereotype.Service;

/**
 * @author: jamen
 * @Date: 2018/12/16 15:30
 * Describe:留言中点赞业务操作
 */
public interface LeaveMessageLikesRecordService {

    /**
     * 是否点赞
     * @param pageName 文章页
     * @param pId 父id
     * @param likeId 当前用户id
     * @return true -- 已经点过赞  false -- 还没有点过赞
     */
    boolean isLiked(String pageName, int pId, int likeId);

    /**
     * 保存点赞记录
     * @param leaveMessageLikesRecord
     */
    void insertLeaveMessageLikesRecord(LeaveMessageLikesRecord leaveMessageLikesRecord);

}
