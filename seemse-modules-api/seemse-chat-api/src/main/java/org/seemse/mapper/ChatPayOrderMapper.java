package org.seemse.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.ChatPayOrder;
import org.seemse.domain.vo.ChatPayOrderVo;

/**
 * 支付订单Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface ChatPayOrderMapper extends BaseMapperPlus<ChatPayOrder, ChatPayOrderVo> {

}
