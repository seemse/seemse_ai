package org.seemse.aihuman.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.aihuman.domain.AihumanInfo;
import org.seemse.aihuman.domain.vo.AihumanInfoVo;

/**
 * AI人类交互信息Mapper接口
 *
 * @author QingYunAI
 */
@Mapper
public interface AihumanInfoMapper extends BaseMapperPlus<AihumanInfo, AihumanInfoVo> {

}
