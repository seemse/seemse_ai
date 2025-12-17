package org.seemse.generator.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.generator.domain.SchemaField;
import org.seemse.generator.domain.vo.SchemaFieldVo;

/**
 * 数据模型字段Mapper接口
 *
 * @author seemse
 * @date 2024-01-01
 */
@Mapper
public interface SchemaFieldMapper extends BaseMapperPlus<SchemaField, SchemaFieldVo> {

}
