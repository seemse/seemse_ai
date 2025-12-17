package org.seemse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.KnowledgeRoleGroup;
import org.seemse.domain.vo.KnowledgeRoleGroupVo;

/**
 * 知识库角色组Mapper接口
 *
 * @author ageerle
 * @date 2025-07-19
 */
@Mapper
public interface KnowledgeRoleGroupMapper extends BaseMapperPlus<KnowledgeRoleGroup, KnowledgeRoleGroupVo> {

}
