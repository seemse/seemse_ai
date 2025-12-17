package org.seemse.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.KnowledgeAttach;
import org.seemse.domain.vo.KnowledgeAttachVo;

/**
 * 知识库附件Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface KnowledgeAttachMapper extends BaseMapperPlus<KnowledgeAttach, KnowledgeAttachVo> {

}
