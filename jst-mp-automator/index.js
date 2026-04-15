#!/usr/bin/env node
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';

// Import all 14 tools
import * as launchTool from './src/tools/launch.js';
import * as navigateTool from './src/tools/navigate.js';
import * as tapTool from './src/tools/tap.js';
import * as inputTool from './src/tools/input.js';
import * as scrollTool from './src/tools/scroll.js';
import * as screenshotTool from './src/tools/screenshot.js';
import * as getPageDataTool from './src/tools/get-page-data.js';
import * as getElementTool from './src/tools/get-element.js';
import * as getConsoleTool from './src/tools/get-console.js';
import * as mockLoginTool from './src/tools/mock-login.js';
import * as queryDbTool from './src/tools/query-db.js';
import * as execDbTool from './src/tools/exec-db.js';
import * as runFlowTool from './src/tools/run-flow.js';
import * as runFlowListTool from './src/tools/run-flow-list.js';

const tools = [
  launchTool,
  navigateTool,
  tapTool,
  inputTool,
  scrollTool,
  screenshotTool,
  getPageDataTool,
  getElementTool,
  getConsoleTool,
  mockLoginTool,
  queryDbTool,
  execDbTool,
  runFlowTool,
  runFlowListTool,
];

const server = new McpServer({ name: 'jst-mp-automator', version: '1.0.0' });

// Register each tool
for (const tool of tools) {
  server.tool(tool.name, tool.description, tool.inputSchema.shape, async (args) => {
    try {
      return await tool.handler(args);
    } catch (err) {
      return {
        content: [{ type: 'text', text: `Error: ${err.message || String(err)}` }],
        isError: true,
      };
    }
  });
}

const transport = new StdioServerTransport();
await server.connect(transport);
console.error('[jst-mp-automator] MCP server started (stdio)');
