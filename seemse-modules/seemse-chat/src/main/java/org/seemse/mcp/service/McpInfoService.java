package org.seemse.mcp.service;

    import org.seemse.core.page.TableDataInfo;
    import org.seemse.core.page.PageQuery;
    import org.seemse.domain.McpInfo;
    import org.seemse.domain.bo.McpInfoBo;
    import org.seemse.domain.vo.McpInfoVo;
    import org.seemse.mcp.config.McpConfig;
    import org.seemse.mcp.config.McpServerConfig;
    import org.seemse.mcp.domain.McpInfoRequest;

    import java.util.Collection;
import java.util.List;

/**
 * MCPService接口
 *
 * @author ageerle
 * @date Sat Aug 09 16:50:58 CST 2025
 */
public interface McpInfoService {

    /**
     * 查询MCP
     */
        McpInfoVo queryById(Integer mcpId);

        /**
         * 查询MCP列表
         */
        TableDataInfo<McpInfoVo> queryPageList(McpInfoBo bo, PageQuery pageQuery);

    /**
     * 查询MCP列表
     */
    List<McpInfoVo> queryList(McpInfoBo bo);

    /**
     * 新增MCP
     */
    Boolean insertByBo(McpInfoBo bo);

    /**
     * 修改MCP
     */
    Boolean updateByBo(McpInfoBo bo);

    /**
     * 校验并批量删除MCP信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);

    McpServerConfig getToolConfigByName(String serverName);

    McpConfig getAllActiveMcpConfig();

    List<String> getActiveServerNames();

    McpInfo saveToolConfig(McpInfoRequest request);

    boolean deleteToolConfig(String serverName);

    boolean updateToolStatus(String serverName, Boolean status);

    boolean enableTool(String serverName);

    boolean disableTool(String serverName);
}
