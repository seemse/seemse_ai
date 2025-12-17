package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysUserPost;

/**
 * 用户与岗位关联表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysUserPostMapper extends BaseMapperPlus<SysUserPost, SysUserPost> {

}
