package com.faber.buzz.msg.biz;

import com.faber.buzz.msg.entity.Msg;
import com.faber.buzz.msg.mapper.MsgMapper;
import com.faber.buzz.msg.vo.MsgStatisticVO;
import com.faber.core.web.biz.BaseBiz;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 系统-消息
 *
 * @author Farando
 * @email faberxu@gmail.com
 * @date 2020-12-13 21:19:53
 */
@Service
public class MsgBiz extends BaseBiz<MsgMapper, Msg> {

    /**
     * 消息数量统计。
     * 1. 未读消息数量；
     */
    public MsgStatisticVO countMine() {
        // 1. 未读消息数量
        long unreadCount = lambdaQuery()
                .eq(Msg::getToUserId, getCurrentUserId())
                .eq(Msg::getIsRead, false)
                .count();

        MsgStatisticVO vo = new MsgStatisticVO();
        vo.setUnreadCount(unreadCount);
        return vo;
    }

    /**
     * 消息批量已读
     * @param ids
     */
    public void batchRead(List<Long> ids) {

        Date now = new Date();
        lambdaUpdate()
                .in(Msg::getId, ids)
                .set(Msg::getIsRead, true)
                .set(Msg::getReadTime, now)
                .update();
    }

    public void sendMsg(String fromUserId, String toUserId, String content) {
        Msg bean = new Msg();
        bean.setFromUserId(fromUserId);
        bean.setToUserId(toUserId);
        bean.setContent(content);
        bean.setIsRead(true);

        this.save(bean);
    }

}
