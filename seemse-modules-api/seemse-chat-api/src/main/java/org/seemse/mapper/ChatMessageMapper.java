package org.seemse.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.ChatMessage;
import org.seemse.domain.vo.ChatMessageVo;

/**
 * 聊天消息Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface ChatMessageMapper extends BaseMapperPlus<ChatMessage, ChatMessageVo> {

}
