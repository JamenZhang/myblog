package com.jamen.model;

import lombok.Data;

/**
 * @author: jamen
 * @Date: 2018/12/23 17:17
 * Describe: 反馈
 */
@Data
public class FeedBack {

    private int id;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 反馈人
     */
    private int personId;

    /**
     * 反馈时间
     */
    private String feedbackDate;

}
