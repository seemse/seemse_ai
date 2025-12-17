package org.seemse.mcpserve;

import org.seemse.mcpserve.service.ToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ageer
 */
@SpringBootApplication
public class SeemseMcpServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeemseMcpServeApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider systemTools(ToolService toolService) {
        return MethodToolCallbackProvider.builder().toolObjects(toolService).build();
    }

}
