package org.seemse.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.seemse.common.core.validate.AddGroup;
import org.seemse.common.core.validate.EditGroup;
import org.seemse.core.domain.BaseEntity;
import org.seemse.domain.KnowledgeRoleGroup;

/**
 * 知识库角色组业务对象 knowledge_role_group
 *
 * @author ageerle
 * @date 2025-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = KnowledgeRoleGroup.class, reverseConvertGenerate = false)
public class KnowledgeRoleGroupBo extends BaseEntity {

    /**
     * 知识库角色组id
     */
    @NotNull(message = "知识库角色组id不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 知识库角色组name
     */
    @NotBlank(message = "知识库角色组name不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 备注
     */
    private String remark;


}
