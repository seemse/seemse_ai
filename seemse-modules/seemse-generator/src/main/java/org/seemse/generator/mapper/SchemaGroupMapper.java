package org.seemse.generator.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.generator.domain.SchemaGroup;
import org.seemse.generator.domain.vo.SchemaGroupVo;

/**
 * 数据模型分组Mapper接口
 *
 * @author seemse
 * @date 2024-01-01
 */
@Mapper
public interface SchemaGroupMapper extends BaseMapperPlus<SchemaGroup, SchemaGroupVo> {

}
