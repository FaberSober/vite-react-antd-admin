package com.faber.buzz.admin.biz;

import com.faber.buzz.admin.entity.BizFile;
import com.faber.buzz.admin.mapper.BizFileMapper;
import com.faber.common.web.biz.BaseBiz;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * html文章
 *
 * @author Farando
 * @email faberxu@gmail.com
 * @date 2019-08-17 20:15:13
 */
@Service
public class BizFileBiz extends BaseBiz<BizFileMapper, BizFile> {

    public List<BizFile> getByBizId(Object bizId, BizFile.BizType bizType) {
        return lambdaQuery()
                .eq(BizFile::getBizId, bizId)
                .eq(BizFile::getBizType, bizType.value)
                .orderByAsc(BizFile::getId)
                .list();
    }

}