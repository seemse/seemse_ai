package org.seemse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.seemse.common.core.utils.MapstructUtils;
import org.seemse.common.core.utils.StringUtils;
import org.seemse.core.page.PageQuery;
import org.seemse.core.page.TableDataInfo;
import org.seemse.domain.ChatPayOrder;
import org.seemse.domain.bo.ChatPayOrderBo;
import org.seemse.domain.vo.ChatPayOrderVo;
import org.seemse.mapper.ChatPayOrderMapper;
import org.seemse.service.IChatPayOrderService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 支付订单Service业务层处理
 *
 * @author ageerle
 * @date 2025-04-08
 */
@RequiredArgsConstructor
@Service
public class ChatPayOrderServiceImpl implements IChatPayOrderService {

    private final ChatPayOrderMapper baseMapper;

    /**
     * 查询支付订单
     */
    @Override
    public ChatPayOrderVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询支付订单列表
     */
    @Override
    public TableDataInfo<ChatPayOrderVo> queryPageList(ChatPayOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChatPayOrder> lqw = buildQueryWrapper(bo);
        Page<ChatPayOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询支付订单列表
     */
    @Override
    public List<ChatPayOrderVo> queryList(ChatPayOrderBo bo) {
        LambdaQueryWrapper<ChatPayOrder> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ChatPayOrder> buildQueryWrapper(ChatPayOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ChatPayOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), ChatPayOrder::getOrderNo, bo.getOrderNo());
        lqw.like(StringUtils.isNotBlank(bo.getOrderName()), ChatPayOrder::getOrderName, bo.getOrderName());
        lqw.eq(bo.getAmount() != null, ChatPayOrder::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getPaymentStatus()), ChatPayOrder::getPaymentStatus, bo.getPaymentStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getPaymentMethod()), ChatPayOrder::getPaymentMethod, bo.getPaymentMethod());
        lqw.eq(bo.getUserId() != null, ChatPayOrder::getUserId, bo.getUserId());
        return lqw;
    }

    /**
     * 新增支付订单
     */
    @Override
    public Boolean insertByBo(ChatPayOrderBo bo) {
        ChatPayOrder add = MapstructUtils.convert(bo, ChatPayOrder.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改支付订单
     */
    @Override
    public Boolean updateByBo(ChatPayOrderBo bo) {
        ChatPayOrder update = MapstructUtils.convert(bo, ChatPayOrder.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ChatPayOrder entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除支付订单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
