package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysTenantPackage;
import org.seemse.system.domain.vo.SysTenantPackageVo;

/**
 * 租户套餐Mapper接口
 *
 * @author Michelle.Chung
 */
@Mapper
public interface SysTenantPackageMapper extends BaseMapperPlus<SysTenantPackage, SysTenantPackageVo> {

}
