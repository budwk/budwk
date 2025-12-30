#!/bin/bash

# MCP 接口测试脚本
# 使用方法: ./test-mcp.sh [BASE_URL]
# 示例: ./test-mcp.sh http://localhost:8080

BASE_URL=${1:-http://localhost:9900}
MCP_ENDPOINT="$BASE_URL/mcp"

echo "========================================="
echo "MCP 接口测试"
echo "端点: $MCP_ENDPOINT"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试函数
test_request() {
    local test_name=$1
    local request_data=$2
    
    echo -e "${YELLOW}测试: $test_name${NC}"
    echo "请求:"
    echo "$request_data" | jq '.' 2>/dev/null || echo "$request_data"
    echo ""
    
    response=$(curl -s -X POST "$MCP_ENDPOINT" \
        -H "Content-Type: application/json" \
        -d "$request_data")
    
    echo "响应:"
    echo "$response" | jq '.' 2>/dev/null || echo "$response"
    echo ""
    
    # 检查是否有错误
    if echo "$response" | jq -e '.error' > /dev/null 2>&1; then
        echo -e "${RED}✗ 测试失败${NC}"
    else
        echo -e "${GREEN}✓ 测试通过${NC}"
    fi
    echo "========================================="
    echo ""
}

# 1. 测试服务状态 (GET)
echo -e "${YELLOW}测试: 服务状态检查 (GET)${NC}"
response=$(curl -s -X GET "$MCP_ENDPOINT")
echo "响应:"
echo "$response" | jq '.' 2>/dev/null || echo "$response"
echo ""
if [ -n "$response" ]; then
    echo -e "${GREEN}✓ 服务运行正常${NC}"
else
    echo -e "${RED}✗ 服务未响应${NC}"
fi
echo "========================================="
echo ""

# 2. 测试 initialize
test_request "初始化连接" '{
  "jsonrpc": "2.0",
  "method": "initialize",
  "params": {},
  "id": 1
}'

# 3. 测试 tools/list
test_request "列出所有工具" '{
  "jsonrpc": "2.0",
  "method": "tools/list",
  "id": 2
}'

# 4. 测试 resources/list
test_request "列出所有资源" '{
  "jsonrpc": "2.0",
  "method": "resources/list",
  "id": 3
}'

# 5. 测试 prompts/list
test_request "列出所有提示" '{
  "jsonrpc": "2.0",
  "method": "prompts/list",
  "id": 4
}'

# 6. 测试 resources/read
test_request "读取 OpenAPI Schema 资源" '{
  "jsonrpc": "2.0",
  "method": "resources/read",
  "params": {
    "uri": "openapi://schema"
  },
  "id": 5
}'

# 7. 测试 prompts/get
test_request "获取认证提示" '{
  "jsonrpc": "2.0",
  "method": "prompts/get",
  "params": {
    "name": "how_to_authenticate"
  },
  "id": 6
}'

# 8. 测试 prompts/get with arguments
test_request "获取 API 概览提示" '{
  "jsonrpc": "2.0",
  "method": "prompts/get",
  "params": {
    "name": "api_overview"
  },
  "id": 7
}'

# 9. 测试错误处理 - 无效方法
test_request "错误处理: 无效方法" '{
  "jsonrpc": "2.0",
  "method": "invalid_method",
  "id": 8
}'

# 10. 测试错误处理 - 缺少参数
test_request "错误处理: 缺少必需参数" '{
  "jsonrpc": "2.0",
  "method": "resources/read",
  "params": {},
  "id": 9
}'

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}测试完成！${NC}"
echo -e "${GREEN}=========================================${NC}"
