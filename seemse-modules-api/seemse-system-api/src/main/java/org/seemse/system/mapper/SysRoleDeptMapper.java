package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysRoleDept;

/**
 * 角色与部门关联表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapperPlus<SysRoleDept, SysRoleDept> {

}
