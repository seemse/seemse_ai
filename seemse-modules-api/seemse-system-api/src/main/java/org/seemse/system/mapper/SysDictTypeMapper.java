package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysDictType;
import org.seemse.system.domain.vo.SysDictTypeVo;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapperPlus<SysDictType, SysDictTypeVo> {

}
