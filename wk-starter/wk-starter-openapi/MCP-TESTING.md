# MCP 接口测试指南

## 前提条件

1. **启动应用服务**
   - 确保应用已启动并运行
   - MCP 功能已在配置中启用

2. **安装测试工具**（可选）
   ```bash
   # 安装 jq 用于格式化 JSON 输出
   brew install jq  # macOS
   # 或
   apt-get install jq  # Linux
   ```

## 测试方法

### 方法一：使用测试脚本（推荐）

我们提供了一个自动化测试脚本 `test-mcp.sh`：

```bash
# 在 wk-starter-openapi 目录下执行
./test-mcp.sh http://localhost:9900
```

该脚本会自动测试所有 MCP 端点，包括：
- ✓ 服务状态检查
- ✓ 初始化连接
- ✓ 列出工具
- ✓ 列出资源
- ✓ 列出提示
- ✓ 读取资源
- ✓ 获取提示
- ✓ 错误处理

### 方法二：使用 curl 手动测试

#### 1. 检查服务状态

```bash
curl http://localhost:9900/mcp
```

**预期响应:**
```json
{
  "status": "MCP server is running",
  "protocol": "JSON-RPC 2.0"
}
```

#### 2. 初始化连接

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "initialize",
    "params": {},
    "id": 1
  }'
```

**预期响应:**
```json
{
  "jsonrpc": "2.0",
  "result": {
    "protocolVersion": "2024-11-05",
    "serverInfo": {
      "name": "BudWk API Server",
      "version": "1.0.0"
    },
    "capabilities": {
      "tools": {
        "listChanged": false
      },
      "resources": {
        "subscribe": false,
        "listChanged": false
      },
      "prompts": {
        "listChanged": false
      }
    }
  },
  "id": 1
}
```

#### 3. 列出所有工具

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/list",
    "id": 2
  }'
```

**预期响应:**
```json
{
  "jsonrpc": "2.0",
  "result": {
    "tools": [
      {
        "name": "UserModule_login",
        "description": "[用户管理] 用户登录",
        "inputSchema": {
          "type": "object",
          "properties": {
            "username": {
              "type": "string",
              "description": "用户名"
            },
            "password": {
              "type": "string",
              "description": "密码"
            }
          },
          "required": ["username", "password"]
        }
      }
      // ... 更多工具
    ]
  },
  "id": 2
}
```

#### 4. 列出所有资源

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "resources/list",
    "id": 3
  }'
```

#### 5. 读取 OpenAPI Schema

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "resources/read",
    "params": {
      "uri": "openapi://schema"
    },
    "id": 4
  }'
```

#### 6. 列出所有提示

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "prompts/list",
    "id": 5
  }'
```

#### 7. 获取认证提示

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "prompts/get",
    "params": {
      "name": "how_to_authenticate"
    },
    "id": 6
  }'
```

#### 8. 获取 API 概览

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "prompts/get",
    "params": {
      "name": "api_overview"
    },
    "id": 7
  }'
```

### 方法三：使用 Postman 测试

1. 创建新的 POST 请求
2. URL: `http://localhost:9900/mcp`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "jsonrpc": "2.0",
     "method": "initialize",
     "params": {},
     "id": 1
   }
   ```

### 方法四：使用 MCP Inspector（官方工具）

MCP 官方提供了一个调试工具：

```bash
# 安装 MCP Inspector
npm install -g @modelcontextprotocol/inspector

# 连接到 MCP 服务器
mcp-inspector http://localhost:9900/mcp
```

## 错误处理测试

### 测试无效方法

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "invalid_method",
    "id": 1
  }'
```

**预期响应:**
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32603,
    "message": "Method not found: invalid_method"
  },
  "id": 1
}
```

### 测试缺少参数

```bash
curl -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "resources/read",
    "params": {},
    "id": 1
  }'
```

**预期响应:**
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32603,
    "message": "Resource URI is required"
  },
  "id": 1
}
```

## 与 AI 应用集成测试

### Claude Desktop 配置

编辑 `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "budwk-api": {
      "url": "http://localhost:9900/mcp"
    }
  }
}
```

重启 Claude Desktop 后，应该能看到 BudWk API 的工具。

### Cursor IDE 配置

在 Cursor 设置中添加 MCP 服务器：

```json
{
  "mcp.servers": [
    {
      "name": "BudWk API",
      "url": "http://localhost:9900/mcp"
    }
  ]
}
```

## 常见问题

### Q: 服务无响应
**A:** 检查：
1. 应用是否已启动
2. MCP 是否在配置中启用 (`openapi.mcp.enable=true`)
3. 端口是否正确

### Q: 返回 404
**A:** 确认：
1. URL 路径是否正确 (`/mcp`)
2. 使用 POST 方法（除了状态检查用 GET）

### Q: 工具列表为空
**A:** 检查：
1. 是否有使用 `@ApiDefinition` 和 `@ApiOperation` 注解的控制器
2. 扫描包路径配置是否正确

## 性能测试

使用 Apache Bench 进行压力测试：

```bash
# 创建测试数据文件
echo '{
  "jsonrpc": "2.0",
  "method": "tools/list",
  "id": 1
}' > test-data.json

# 执行压力测试
ab -n 1000 -c 10 -p test-data.json -T application/json \
  http://localhost:9900/mcp
```

## 日志查看

查看 MCP 相关日志：

```bash
# 查看应用日志
tail -f logs/application.log | grep MCP
```

## 下一步

测试通过后，您可以：
1. 配置 AI 应用连接到 MCP 服务器
2. 在 AI 应用中使用暴露的工具
3. 让 AI 读取 API 文档资源
4. 使用提示模板获取 API 使用指导
