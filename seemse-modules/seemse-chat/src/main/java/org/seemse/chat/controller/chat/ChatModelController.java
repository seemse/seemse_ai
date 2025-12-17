package org.seemse.chat.controller.chat;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.seemse.chat.enums.DisplayType;
import org.seemse.common.core.domain.R;
import org.seemse.common.core.validate.AddGroup;
import org.seemse.common.core.validate.EditGroup;
import org.seemse.common.excel.utils.ExcelUtil;
import org.seemse.common.idempotent.annotation.RepeatSubmit;
import org.seemse.common.log.annotation.Log;
import org.seemse.common.log.enums.BusinessType;
import org.seemse.common.web.core.BaseController;
import org.seemse.core.page.PageQuery;
import org.seemse.core.page.TableDataInfo;
import org.seemse.domain.ChatModel;
import org.seemse.domain.bo.ChatModelBo;
import org.seemse.domain.vo.ChatModelVo;
import org.seemse.service.IChatModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天模型
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/model")
public class ChatModelController extends BaseController {

    private final IChatModelService chatModelService;

    /**
     * 查询聊天模型列表
     */
    @GetMapping("/list")
    public TableDataInfo<ChatModelVo> list(ChatModelBo bo, PageQuery pageQuery) {
        return chatModelService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询用户模型列表
     */
    @GetMapping("/modelList")
    public R<List<ChatModelVo>> modelList(ChatModelBo bo) {
        bo.setModelShow(DisplayType.VISIBLE.getCode());
        return R.ok(chatModelService.queryList(bo));
    }


    /**
     * 查询ppt模型信息
     */
    @GetMapping("/getPPT")
    public R<ChatModel> getPPT() {
        return R.ok(chatModelService.getPPT());
    }

    /**
     * 导出聊天模型列表
     */
    @Log(title = "聊天模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ChatModelBo bo, HttpServletResponse response) {
        List<ChatModelVo> list = chatModelService.queryList(bo);
        ExcelUtil.exportExcel(list, "聊天模型", ChatModelVo.class, response);
    }

    /**
     * 获取聊天模型详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<ChatModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(chatModelService.queryById(id));
    }

    /**
     * 新增聊天模型
     */
    @SaCheckPermission("system:model:add")
    @Log(title = "聊天模型", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ChatModelBo bo) {
        return toAjax(chatModelService.insertByBo(bo));
    }

    /**
     * 修改聊天模型
     */
    @SaCheckPermission("system:model:edit")
    @Log(title = "聊天模型", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ChatModelBo bo) {
        return toAjax(chatModelService.updateByBo(bo));
    }

    /**
     * 删除聊天模型
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:model:remove")
    @Log(title = "聊天模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(chatModelService.deleteWithValidByIds(List.of(ids), true));
    }
}
