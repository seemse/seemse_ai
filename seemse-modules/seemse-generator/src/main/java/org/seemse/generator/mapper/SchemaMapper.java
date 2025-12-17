package org.seemse.generator.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.generator.domain.Schema;
import org.seemse.generator.domain.vo.SchemaVo;

/**
 * 数据模型Mapper接口
 *
 * @author seemse
 * @date 2024-01-01
 */
@Mapper
public interface SchemaMapper extends BaseMapperPlus<Schema, SchemaVo> {

}
