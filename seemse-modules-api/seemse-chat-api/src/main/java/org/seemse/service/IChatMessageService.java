package org.seemse.service;


import org.seemse.core.page.PageQuery;
import org.seemse.core.page.TableDataInfo;
import org.seemse.domain.bo.ChatMessageBo;
import org.seemse.domain.vo.ChatMessageVo;

import java.util.Collection;
import java.util.List;

/**
 * 聊天消息Service接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
public interface IChatMessageService {

    /**
     * 查询聊天消息
     */
    ChatMessageVo queryById(Long id);

    /**
     * 查询聊天消息列表
     */
    TableDataInfo<ChatMessageVo> queryPageList(ChatMessageBo bo, PageQuery pageQuery);

    /**
     * 查询聊天消息列表
     */
    List<ChatMessageVo> queryList(ChatMessageBo bo);

    /**
     * 新增聊天消息
     */
    Boolean insertByBo(ChatMessageBo bo);

    /**
     * 修改聊天消息
     */
    Boolean updateByBo(ChatMessageBo bo);

    /**
     * 校验并批量删除聊天消息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
