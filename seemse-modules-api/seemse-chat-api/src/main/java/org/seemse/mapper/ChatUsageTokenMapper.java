package org.seemse.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.ChatUsageToken;
import org.seemse.domain.vo.ChatUsageTokenVo;

/**
 * 用户token使用详情Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface ChatUsageTokenMapper extends BaseMapperPlus<ChatUsageToken, ChatUsageTokenVo> {

}
