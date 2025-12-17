package org.seemse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.PromptTemplate;
import org.seemse.domain.vo.PromptTemplateVo;

/**
 * 提示词模板Mapper接口
 *
 * @author evo
 * @date 2025-06-12
 */
@Mapper
public interface PromptTemplateMapper extends BaseMapperPlus<PromptTemplate, PromptTemplateVo> {

}
