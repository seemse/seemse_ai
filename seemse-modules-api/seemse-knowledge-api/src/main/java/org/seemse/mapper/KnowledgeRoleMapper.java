package org.seemse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.domain.KnowledgeRole;
import org.seemse.domain.vo.KnowledgeRoleVo;

/**
 * 知识库角色Mapper接口
 *
 * @author ageerle
 * @date 2025-07-19
 */
@Mapper
public interface KnowledgeRoleMapper extends BaseMapperPlus<KnowledgeRole, KnowledgeRoleVo> {

}
