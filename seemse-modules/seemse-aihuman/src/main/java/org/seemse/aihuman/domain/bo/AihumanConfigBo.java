package org.seemse.aihuman.domain.bo;

import org.seemse.aihuman.domain.AihumanConfig;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import org.seemse.common.core.validate.AddGroup;
import org.seemse.common.core.validate.EditGroup;

/**
 * 交互数字人配置业务对象 aihuman_config
 *
 * @author ageerle
 * @date Fri Sep 26 22:27:00 GMT+08:00 2025
 */
@Data

@AutoMapper(target = AihumanConfig.class, reverseConvertGenerate = false)
public class AihumanConfigBo implements Serializable {

    private Integer id;

    /**
     * name
     */
    private String name;
    /**
     * modelName
     */
    private String modelName;
    /**
     * modelPath
     */
    private String modelPath;
    /**
     * modelParams
     */
    private String modelParams;
    /**
     * agentParams
     */
    private String agentParams;
    /**
     * createTime
     */
    private LocalDateTime createTime;
    /**
     * updateTime
     */
    private LocalDateTime updateTime;
    /**
     * status
     */
    @NotNull(message = "status不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;
    /**
     * publish
     */
    @NotNull(message = "publish不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer publish;

}
