package com.jamen.mapper;

import com.jamen.model.PrivateWord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: jamen
 * @Date: 2018/12/22 20:22
 * Describe: 悄悄话sql
 */
@Mapper
@Repository
public interface PrivateWordMapper {

    @Insert("insert into privateword(privateWord,publisherId,replierId,replyContent,publisherDate) " +
            "values(#{privateWord},#{publisherId},#{replierId},#{replyContent},#{publisherDate})")
    void publishPrivateWord(PrivateWord privateWord);

    @Select("select * from privateword where publisherId=#{publisherId} order by id desc")
    List<PrivateWord> getPrivateWordByPublisher(@Param("publisherId") int publisherId);

    @Select("select * from privateword")
    List<PrivateWord> getAllPrivateWord();

    @Update("update privateword set replierId=#{replierId},replyContent=#{replyContent} where id=#{id}")
    void replyPrivateWord(@Param("replyContent") String replyContent, @Param("replierId") int replierId, @Param("id") int id);

}
