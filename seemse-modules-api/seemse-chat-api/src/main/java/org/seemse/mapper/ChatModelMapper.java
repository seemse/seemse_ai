package org.seemse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.ChatModel;
import org.seemse.domain.vo.ChatModelVo;

/**
 * 聊天模型Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface ChatModelMapper extends BaseMapperPlus<ChatModel, ChatModelVo> {

}
