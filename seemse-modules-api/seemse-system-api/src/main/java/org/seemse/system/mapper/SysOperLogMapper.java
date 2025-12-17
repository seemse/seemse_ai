package org.seemse.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.seemse.core.mapper.BaseMapperPlus;
import org.seemse.system.domain.SysOperLog;
import org.seemse.system.domain.vo.SysOperLogVo;

/**
 * 操作日志 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLog, SysOperLogVo> {

}
