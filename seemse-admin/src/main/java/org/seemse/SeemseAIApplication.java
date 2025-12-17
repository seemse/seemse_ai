package org.seemse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

;

@SpringBootApplication(scanBasePackages = {"org.seemse", "org.seemse.aihuman"})
@EnableScheduling
@EnableAsync
public class SeemseAIApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SeemseAIApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeemseAIApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(2048));
        app.run(args);
        System.out.println("==============================  启动成功   ==============================ﾞ");
    }
}
