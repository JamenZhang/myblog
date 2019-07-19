package com.jamen.mapper;

import com.jamen.model.ArticleLikesRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: jamen
 * @Date: 2018/12/7 15:51
 * Describe: 文章点赞sql
 */
@Mapper
@Repository
public interface ArticleLikesMapper {

    @Insert("insert into article_likes_record(articleId,likerId,likeDate,isRead) values(#{articleId},#{likerId},#{likeDate},#{isRead})")
    void insertArticleLikesRecord(ArticleLikesRecord articleLikesRecord);

    @Select("select likeDate from article_likes_record where articleId=#{articleId} and likerId=#{likerId}")
    ArticleLikesRecord isLiked(@Param("articleId") long articleId, @Param("likerId") int likerId);

    @Delete("delete from article_likes_record where articleId=#{articleId}")
    void deleteArticleLikesRecordByArticleId(long articleId);

    @Select("select * from article_likes_record where likerId<>#{likerId} order by id desc")
    List<ArticleLikesRecord> getArticleThumbsUp(int likerId);

    @Select("select count(*) from article_likes_record where isRead=1")
    int countIsReadNum();

    @Update("update article_likes_record set isRead=0 where id=#{id}")
    void readThisThumbsUp(int id);

    @Update("update article_likes_record set isRead=0")
    void readAllThumbsUp();
}
