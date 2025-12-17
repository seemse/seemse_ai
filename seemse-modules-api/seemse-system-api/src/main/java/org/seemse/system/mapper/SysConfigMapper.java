package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysConfig;
import org.seemse.system.domain.vo.SysConfigVo;

/**
 * 参数配置 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysConfigMapper extends BaseMapperPlus<SysConfig, SysConfigVo> {

}
