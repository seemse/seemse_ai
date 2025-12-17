package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysTenant;
import org.seemse.system.domain.vo.SysTenantVo;

/**
 * 租户Mapper接口
 *
 * @author Michelle.Chung
 */
@Mapper
public interface SysTenantMapper extends BaseMapperPlus<SysTenant, SysTenantVo> {

}
