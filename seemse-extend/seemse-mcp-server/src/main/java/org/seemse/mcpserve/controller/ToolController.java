package org.seemse.mcpserve.controller;

import org.seemse.mcpserve.service.ToolService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author elesmal
 * @date 2025-11-20 9:27
 */
@RestController
public class ToolController {

    private final ApplicationContext applicationContext;

    public ToolController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/tools")
    public List<Map<String, Object>> listTools() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // 扫描所有带 @Service 的 Bean
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Service.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();

            // 遍历所有方法
            for (Method method : clazz.getDeclaredMethods()) {
                Tool toolAnn = method.getAnnotation(Tool.class);
                if (toolAnn != null) {
                    Map<String, Object> toolDef = buildToolDefinition(method, toolAnn);
                    tools.add(toolDef);
                }
            }
        }
        return tools;
    }

    private Map<String, Object> buildToolDefinition(Method method, Tool toolAnn) {
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        // 分析参数（简化：只支持单个 String 参数或无参）
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            // 无参
        } else if (paramTypes.length == 1 && paramTypes[0] == String.class) {
            // 假设参数名为 "input" 或根据注解
            String paramName = "input";
            ToolParam paramAnn = method.getParameters()[0].getAnnotation(ToolParam.class);
            if (paramAnn != null && !paramAnn.description().isEmpty()) {
                paramName = "input"; // 或从注解提取，但 OpenAI 不强制参数名
            }
            properties.put(paramName, Map.of("type", "string", "description",
                    paramAnn != null ? paramAnn.description() : "输入参数"));
            required.add(paramName);
        } else {
            // 更复杂类型可后续扩展（如 JSON 对象）
            properties.put("args", Map.of("type", "object", "description", "参数对象"));
            required.add("args");
        }

        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", method.getName(),
                        "description", toolAnn.description(),
                        "parameters", Map.of(
                                "type", "object",
                                "properties", properties,
                                "required", required
                        )
                )
        );
    }

    @PostMapping("/invoke")
    public ResponseEntity<Map<String, Object>> invoke(@RequestBody Map<String, Object> request) {
        String methodName = (String) request.get("method");
        Map<String, Object> params = (Map<String, Object>) request.getOrDefault("params", Collections.emptyMap());

        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Service.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();

            for (Method method : clazz.getDeclaredMethods()) {
                if (!methodName.equals(method.getName())) continue;
                if (method.getAnnotation(Tool.class) == null) continue;

                try {
                    Object[] args = buildArguments(method, params);
                    Object result = method.invoke(bean, args);
                    return ok(result, request);
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(Map.of(
                            "jsonrpc", "2.0",
                            "error", Map.of("code", -32603, "message", "Internal error: " + e.getCause().getMessage()),
                            "id", request.getOrDefault("id", "1")
                    ));
                }
            }
        }

        return ResponseEntity.badRequest().body(Map.of(
                "jsonrpc", "2.0",
                "error", Map.of("code", -32601, "message", "Method not found: " + methodName),
                "id", request.getOrDefault("id", "1")
        ));
    }

    private Object[] buildArguments(Method method, Map<String, Object> params) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName(); // 需要 -parameters 编译参数！

            if (!params.containsKey(paramName)) {
                throw new IllegalArgumentException("Missing required parameter: " + paramName);
            }

            // 目前只支持 String（可后续扩展）
            if (param.getType() == String.class) {
                args[i] = String.valueOf(params.get(paramName));
            } else {
                throw new IllegalArgumentException("Unsupported parameter type: " + param.getType());
            }
        }
        return args;
    }

    // ✅ 确保这个方法存在！
    private ResponseEntity<Map<String, Object>> ok(Object result, Map<String, Object> req) {
        return ResponseEntity.ok(Map.of(
                "jsonrpc", "2.0",
                "result", result,
                "id", req.getOrDefault("id", "1")
        ));
    }
}
