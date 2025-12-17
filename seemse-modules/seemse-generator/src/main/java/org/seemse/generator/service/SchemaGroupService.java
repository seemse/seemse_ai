package org.seemse.generator.service;


import org.seemse.core.page.PageQuery;
import org.seemse.core.page.TableDataInfo;
import org.seemse.generator.domain.bo.SchemaGroupBo;
import org.seemse.generator.domain.vo.SchemaGroupVo;

import java.util.Collection;
import java.util.List;

/**
 * 数据模型分组Service接口
 *
 * @author seemse
 * @date 2024-01-01
 */
public interface SchemaGroupService {

    /**
     * 查询数据模型分组
     */
    SchemaGroupVo queryById(Long id);

    /**
     * 查询数据模型分组列表
     */
    TableDataInfo<SchemaGroupVo> queryPageList(SchemaGroupBo bo, PageQuery pageQuery);

    /**
     * 查询数据模型分组列表
     */
    List<SchemaGroupVo> queryList(SchemaGroupBo bo);

    /**
     * 新增数据模型分组
     */
    Boolean insertByBo(SchemaGroupBo bo);

    /**
     * 修改数据模型分组
     */
    Boolean updateByBo(SchemaGroupBo bo);

    /**
     * 校验并批量删除数据模型分组信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
