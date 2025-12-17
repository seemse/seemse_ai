package org.seemse.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.KnowledgeFragment;
import org.seemse.domain.vo.KnowledgeFragmentVo;

/**
 * 知识片段Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface KnowledgeFragmentMapper extends BaseMapperPlus<KnowledgeFragment, KnowledgeFragmentVo> {

}
