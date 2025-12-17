package org.seemse.generator.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 数据模型添加事件
 *
 * @author seemse
 */
@Getter
public class SchemaAddedEvent extends ApplicationEvent {

    private final Long schemaId;
    private final String tableName;

    public SchemaAddedEvent(Object source, Long schemaId, String tableName) {
        super(source);
        this.schemaId = schemaId;
        this.tableName = tableName;
    }
}
