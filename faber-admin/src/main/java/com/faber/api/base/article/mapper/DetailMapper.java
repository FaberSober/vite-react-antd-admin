package com.faber.api.base.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.faber.api.base.article.entity.Detail;
import com.faber.api.base.article.vo.BookOutlineDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章-详情
 * 
 * @author Farando
 * @email faberxu@gmail.com
 * @date 2020-12-31 13:53:39
 */
public interface DetailMapper extends BaseMapper<Detail> {

    List<BookOutlineDetail> getByBook(@Param("bookId") Integer bookId);
	
}
