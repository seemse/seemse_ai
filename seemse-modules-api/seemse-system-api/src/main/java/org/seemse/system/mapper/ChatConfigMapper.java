package org.seemse.system.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.ChatConfig;
import org.seemse.system.domain.vo.ChatConfigVo;

/**
 * 配置信息Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface ChatConfigMapper extends BaseMapperPlus<ChatConfig, ChatConfigVo> {

}
