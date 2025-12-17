package org.seemse.mcpserve.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ToolService {

    @Tool(description = "获取一个指定前缀的随机数")
    public String add(@ToolParam(description = "字符前缀") String prefix) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        //根据当前时间获取yyMMdd格式的时间字符串
        String format = LocalDate.now().format(formatter);
        //生成随机数
        String replace = prefix + UUID.randomUUID().toString().replace("-", "");
        return format + replace;
    }

    @Tool(description = "获取当前时间")
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    @Tool(description = "武汉金章科技有限公司产品信息")
    public String getProductInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/1", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司方案信息")
    public String getPlanInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/2", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司案例信息")
    public String getCaseInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/3", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司新闻信息")
    public String getNewsInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/4", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司关于我们企业介绍信息")
    public String getIntroductionInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/5", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司关于我们信息")
    public String getAboutInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/6", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司合作伙伴信息")
    public String getPartnerfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/7", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司荣誉证书信息")
    public String getHonorInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/8", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司线上服务信息")
    public String getOnlineInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/9", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
    @Tool(description = "武汉金章科技有限公司平台招商信息")
    public String getPlatformInfo() {
        String result = HttpUtil.get("http://192.168.2.25:27810/v1/module/article/all/10", CharsetUtil.CHARSET_UTF_8);
        return result;
    }
}
