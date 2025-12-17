package org.seemse.generator.service;


import org.seemse.core.page.PageQuery;
import org.seemse.core.page.TableDataInfo;
import org.seemse.generator.domain.bo.SchemaBo;
import org.seemse.generator.domain.vo.SchemaVo;

import java.util.Collection;
import java.util.List;

/**
 * 数据模型Service接口
 *
 * @author seemse
 * @date 2024-01-01
 */
public interface SchemaService {

    /**
     * 查询数据模型
     */
    SchemaVo queryById(Long id);

    /**
     * 查询数据模型列表
     */
    TableDataInfo<SchemaVo> queryPageList(SchemaBo bo, PageQuery pageQuery);

    /**
     * 查询数据模型列表
     */
    List<SchemaVo> queryList(SchemaBo bo);

    /**
     * 新增数据模型
     */
    Boolean insertByBo(SchemaBo bo);

    /**
     * 修改数据模型
     */
    Boolean updateByBo(SchemaBo bo);

    /**
     * 校验并批量删除数据模型信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据表名查询数据模型
     */
    SchemaVo queryByTableName(String tableName);
}
