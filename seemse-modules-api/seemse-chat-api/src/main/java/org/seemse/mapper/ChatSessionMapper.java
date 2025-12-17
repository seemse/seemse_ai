package org.seemse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.ChatSession;
import org.seemse.domain.vo.ChatSessionVo;

/**
 * 会话管理Mapper接口
 *
 * @author ageerle
 * @date 2025-05-03
 */
@Mapper
public interface ChatSessionMapper extends BaseMapperPlus<ChatSession, ChatSessionVo> {

}
