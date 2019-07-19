package com.jamen.mapper;

import com.jamen.model.Tag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: jamen
 * @Date: 2018/12/16 19:51
 * Describe: 标签sql
 */
@Mapper
@Repository
public interface TagMapper {

    @Insert("insert into tags(tagName,tagSize) values(#{tagName},#{tagSize})")
    void insertTag(Tag tag);

    @Select("select IFNULL(max(id),0) from tags where tagName=#{tagName}")
    int findIsExistByTagName(@Param("tagName") String tagName);

    @Select("select * from tags order by id desc")
    List<Tag> findTagsCloud();

    @Select("select count(*) from tags")
    int countTagsNum();

    @Select("select tagSize from tags where tagName=#{tagName}")
    int getTagsSizeByTagName(String tagName);

}
