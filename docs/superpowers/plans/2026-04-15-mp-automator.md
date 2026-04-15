# 小程序自动化测试 MCP Server 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build `jst-mp-automator` MCP Server that drives WeChat DevTools via `miniprogram-automator`, executes 45 YAML test flows, and generates product experience audit reports.

**Architecture:** Node.js MCP Server (stdio) wrapping `miniprogram-automator` SDK. 14 tools exposed to Claude Code. YAML-based flow engine with precondition checks, step execution, screenshot capture, DB validation, and product review prompts. Reports combine screenshots + page data + DB queries for dual verification.

**Tech Stack:** Node.js 18+, `@modelcontextprotocol/sdk`, `miniprogram-automator`, `mysql2`, `js-yaml`, `zod`

**Spec:** `docs/superpowers/specs/2026-04-15-mp-automator-design.md`

---

## File Structure

```
jst-mp-automator/
├── package.json
├── index.js                    # MCP Server entry (stdio)
├── src/
│   ├── config.js               # Env-based configuration
│   ├── automator.js            # miniprogram-automator wrapper (connect/retry/timeout)
│   ├── db.js                   # MySQL connection pool
│   ├── flow-engine.js          # YAML flow interpreter
│   ├── reporter.js             # Report generator
│   └── tools/
│       ├── launch.js           # mp_launch
│       ├── navigate.js         # mp_navigate
│       ├── tap.js              # mp_tap
│       ├── input.js            # mp_input
│       ├── scroll.js           # mp_scroll
│       ├── screenshot.js       # mp_screenshot (with full-page scroll)
│       ├── get-page-data.js    # mp_get_page_data
│       ├── get-element.js      # mp_get_element
│       ├── get-console.js      # mp_get_console
│       ├── mock-login.js       # mp_mock_login
│       ├── query-db.js         # mp_query_db
│       ├── exec-db.js          # mp_exec_db
│       ├── run-flow.js         # mp_run_flow
│       └── run-flow-list.js    # mp_run_flow_list
├── flows/                      # 45 YAML flow scripts
│   ├── 01-login.yaml
│   ├── ...
│   ├── 45-partner-apply.yaml
│   └── _full-regression.yaml
├── reports/                    # Generated reports (gitignored)
└── screenshots/                # Generated screenshots (gitignored)
```

---

## Task 1: Project Scaffold

**Files:**
- Create: `jst-mp-automator/package.json`
- Create: `jst-mp-automator/src/config.js`
- Create: `jst-mp-automator/.gitignore`

- [ ] **Step 1: Create package.json**

```bash
cd D:/coding/jst_v1
mkdir -p jst-mp-automator/src/tools jst-mp-automator/flows jst-mp-automator/reports jst-mp-automator/screenshots
```

Write `jst-mp-automator/package.json`:

```json
{
  "name": "jst-mp-automator",
  "version": "1.0.0",
  "type": "module",
  "description": "MCP Server for WeChat Mini Program automation testing",
  "main": "index.js",
  "scripts": {
    "start": "node index.js",
    "inspect": "npx @modelcontextprotocol/inspector node index.js"
  },
  "dependencies": {
    "@modelcontextprotocol/sdk": "^1.12.0",
    "miniprogram-automator": "^0.12.1",
    "mysql2": "^3.11.0",
    "js-yaml": "^4.1.0",
    "zod": "^3.24.0",
    "node-fetch": "^3.3.0"
  }
}
```

- [ ] **Step 2: Create config.js**

Write `jst-mp-automator/src/config.js`:

```javascript
const config = {
  // WeChat DevTools
  devtoolsCliPath: process.env.DEVTOOLS_CLI_PATH || 'C:/Program Files (x86)/Tencent/微信web开发者工具/cli.bat',
  wsEndpoint: process.env.WX_WS_ENDPOINT || '',
  projectPath: process.env.PROJECT_PATH || 'D:/coding/jst_v1/jst-uniapp/unpackage/dist/dev/mp-weixin',
  automatorPort: parseInt(process.env.AUTOMATOR_PORT || '9420'),

  // Backend
  backendUrl: process.env.BACKEND_URL || 'http://127.0.0.1:8080',

  // Database
  db: {
    host: process.env.DB_HOST || '59.110.53.165',
    port: parseInt(process.env.DB_PORT || '3306'),
    database: process.env.DB_NAME || 'jst_new',
    user: process.env.DB_USER || 'jst_new',
    password: process.env.DB_PASS || 'J8zZpAa4zG8y6a7e',
    waitForConnections: true,
    connectionLimit: 5,
    charset: 'utf8mb4'
  },

  // Timeouts & retries
  actionTimeout: parseInt(process.env.ACTION_TIMEOUT || '10000'),
  retryCount: parseInt(process.env.RETRY_COUNT || '3'),
  retryBaseDelay: 2000,
  waitAfterNavigate: 2000,
  waitBeforeScreenshot: 1000,

  // Paths
  flowsDir: new URL('../flows', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
  reportsDir: new URL('../reports', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
  screenshotsDir: new URL('../screenshots', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
};

export default config;
```

- [ ] **Step 3: Create .gitignore**

Write `jst-mp-automator/.gitignore`:

```
node_modules/
reports/*.json
screenshots/*.png
```

- [ ] **Step 4: Install dependencies**

```bash
cd D:/coding/jst_v1/jst-mp-automator && npm install
```

Expected: `added N packages`

- [ ] **Step 5: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/package.json jst-mp-automator/src/config.js jst-mp-automator/.gitignore
git commit -m "feat(automator): scaffold MCP server project with dependencies"
```

---

## Task 2: Automator Adapter

**Files:**
- Create: `jst-mp-automator/src/automator.js`

The adapter wraps `miniprogram-automator` with retry logic, timeout handling, and state management (connected/disconnected).

- [ ] **Step 1: Write automator.js**

```javascript
import automator from 'miniprogram-automator';
import config from './config.js';

let miniProgram = null;

async function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function withRetry(fn, label = 'action') {
  let lastError;
  for (let i = 0; i < config.retryCount; i++) {
    try {
      return await Promise.race([
        fn(),
        new Promise((_, reject) =>
          setTimeout(() => reject(new Error(`${label} timed out after ${config.actionTimeout}ms`)), config.actionTimeout)
        )
      ]);
    } catch (err) {
      lastError = err;
      const delay = config.retryBaseDelay * Math.pow(2, i);
      console.error(`[automator] ${label} attempt ${i + 1} failed: ${err.message}, retrying in ${delay}ms`);
      await sleep(delay);
    }
  }
  throw lastError;
}

export async function launch() {
  if (miniProgram) {
    try { await miniProgram.close(); } catch {}
  }

  if (config.wsEndpoint) {
    console.error(`[automator] connecting to ${config.wsEndpoint}`);
    miniProgram = await automator.connect({ wsEndpoint: config.wsEndpoint });
  } else {
    console.error(`[automator] launching DevTools, project: ${config.projectPath}`);
    miniProgram = await automator.launch({
      cliPath: config.devtoolsCliPath,
      projectPath: config.projectPath,
      port: config.automatorPort,
    });
  }
  console.error('[automator] connected');
  return miniProgram;
}

export function getMiniProgram() {
  if (!miniProgram) throw new Error('Not connected. Call mp_launch first.');
  return miniProgram;
}

export async function navigateTo(path, query) {
  const mp = getMiniProgram();
  const url = query ? `${path}?${new URLSearchParams(query).toString()}` : path;
  const isTab = [
    '/pages/index/index',
    '/pages/contest/list',
    '/pages/course/list',
    '/pages/notice/list',
    '/pages/my/index'
  ].includes(path);

  const page = await withRetry(
    () => isTab ? mp.switchTab(url) : mp.navigateTo(url),
    `navigate(${url})`
  );
  await sleep(config.waitAfterNavigate);
  return page;
}

export async function reLaunchTo(path) {
  const mp = getMiniProgram();
  const page = await withRetry(() => mp.reLaunch(path), `reLaunch(${path})`);
  await sleep(config.waitAfterNavigate);
  return page;
}

export async function getCurrentPage() {
  const mp = getMiniProgram();
  return await mp.currentPage();
}

export async function screenshot(savePath) {
  const mp = getMiniProgram();
  await sleep(config.waitBeforeScreenshot);
  if (savePath) {
    await mp.screenshot({ path: savePath });
    return savePath;
  }
  return await mp.screenshot();
}

export async function tapElement(selector) {
  const page = await getCurrentPage();
  const el = await withRetry(async () => {
    const e = await page.$(selector);
    if (!e) throw new Error(`Element not found: ${selector}`);
    return e;
  }, `find(${selector})`);
  await el.tap();
  await sleep(500);
}

export async function inputText(selector, value) {
  const page = await getCurrentPage();
  const el = await withRetry(async () => {
    const e = await page.$(selector);
    if (!e) throw new Error(`Element not found: ${selector}`);
    return e;
  }, `find(${selector})`);
  await el.input(value);
}

export async function scrollPage(direction = 'down', distance = 300) {
  const mp = getMiniProgram();
  const page = await getCurrentPage();
  const current = parseInt(await page.scrollTop()) || 0;
  const target = direction === 'down' ? current + distance : Math.max(0, current - distance);
  await mp.pageScrollTo(target);
  await sleep(500);
}

export async function getPageData(path) {
  const page = await getCurrentPage();
  if (path) return await page.data(path);
  return await page.data();
}

export async function getElement(selector) {
  const page = await getCurrentPage();
  const el = await page.$(selector);
  if (!el) return null;
  const [text, wxml, size, offset] = await Promise.all([
    el.text().catch(() => ''),
    el.outerWxml().catch(() => ''),
    el.size().catch(() => null),
    el.offset().catch(() => null),
  ]);
  return { selector, text, wxml, size, offset, exists: true };
}

export async function evaluate(fn, ...args) {
  const mp = getMiniProgram();
  return await mp.evaluate(fn, ...args);
}

export async function callWxMethod(method, ...args) {
  const mp = getMiniProgram();
  return await mp.callWxMethod(method, ...args);
}

export async function close() {
  if (miniProgram) {
    await miniProgram.close();
    miniProgram = null;
  }
}

export { withRetry, sleep };
```

- [ ] **Step 2: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/src/automator.js
git commit -m "feat(automator): automator adapter with retry/timeout/navigation"
```

---

## Task 3: Database Module

**Files:**
- Create: `jst-mp-automator/src/db.js`

- [ ] **Step 1: Write db.js**

```javascript
import mysql from 'mysql2/promise';
import config from './config.js';

let pool = null;

function getPool() {
  if (!pool) {
    pool = mysql.createPool(config.db);
    console.error(`[db] pool created → ${config.db.host}:${config.db.port}/${config.db.database}`);
  }
  return pool;
}

export async function query(sql) {
  const normalized = sql.trim().toUpperCase();
  if (!normalized.startsWith('SELECT')) {
    throw new Error('mp_query_db only allows SELECT statements. Use mp_exec_db for writes.');
  }
  const [rows] = await getPool().execute(sql);
  return rows;
}

export async function exec(sql) {
  const normalized = sql.trim().toUpperCase();
  if (normalized.startsWith('SELECT')) {
    throw new Error('Use mp_query_db for SELECT. mp_exec_db is for INSERT/UPDATE/DELETE.');
  }
  if (normalized.startsWith('DROP') || normalized.startsWith('TRUNCATE') || normalized.startsWith('ALTER')) {
    throw new Error('DDL statements not allowed. Only INSERT/UPDATE/DELETE.');
  }
  console.error(`[db] exec: ${sql.substring(0, 100)}...`);
  const [result] = await getPool().execute(sql);
  return { affectedRows: result.affectedRows, insertId: result.insertId };
}

export async function closePool() {
  if (pool) {
    await pool.end();
    pool = null;
  }
}
```

- [ ] **Step 2: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/src/db.js
git commit -m "feat(automator): MySQL adapter with query/exec separation"
```

---

## Task 4: MCP Server Entry + Tool Registration

**Files:**
- Create: `jst-mp-automator/index.js`
- Create: `jst-mp-automator/src/tools/launch.js`
- Create: `jst-mp-automator/src/tools/navigate.js`
- Create: `jst-mp-automator/src/tools/tap.js`
- Create: `jst-mp-automator/src/tools/input.js`
- Create: `jst-mp-automator/src/tools/scroll.js`
- Create: `jst-mp-automator/src/tools/screenshot.js`
- Create: `jst-mp-automator/src/tools/get-page-data.js`
- Create: `jst-mp-automator/src/tools/get-element.js`
- Create: `jst-mp-automator/src/tools/get-console.js`
- Create: `jst-mp-automator/src/tools/mock-login.js`
- Create: `jst-mp-automator/src/tools/query-db.js`
- Create: `jst-mp-automator/src/tools/exec-db.js`
- Create: `jst-mp-automator/src/tools/run-flow.js`
- Create: `jst-mp-automator/src/tools/run-flow-list.js`

- [ ] **Step 1: Write all 14 tool files**

Write `jst-mp-automator/src/tools/launch.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_launch';
export const description = 'Launch or connect to WeChat DevTools. Must be called first before any other mp_ tool.';
export const inputSchema = z.object({
  projectPath: z.string().optional().describe('Override project path (default from config)')
});

export async function handler({ projectPath }) {
  const mp = await bot.launch(projectPath);
  const page = await mp.currentPage();
  return { content: [{ type: 'text', text: `Connected. Current page: ${page.path}` }] };
}
```

Write `jst-mp-automator/src/tools/navigate.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_navigate';
export const description = 'Navigate to a mini program page by path. Use reLaunch for a clean navigation that clears the page stack.';
export const inputSchema = z.object({
  path: z.string().describe('Page path, e.g. /pages/index/index or /pages-sub/my/order-list'),
  query: z.record(z.string()).optional().describe('Query parameters'),
  reLaunch: z.boolean().optional().default(false).describe('Use reLaunch to clear page stack')
});

export async function handler({ path, query, reLaunch }) {
  const page = reLaunch
    ? await bot.reLaunchTo(path)
    : await bot.navigateTo(path, query);
  return { content: [{ type: 'text', text: `Navigated to ${page.path}` }] };
}
```

Write `jst-mp-automator/src/tools/tap.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_tap';
export const description = 'Tap (click) an element by CSS selector.';
export const inputSchema = z.object({
  selector: z.string().describe('CSS selector, e.g. .btn-submit or .contest-card:first-child')
});

export async function handler({ selector }) {
  await bot.tapElement(selector);
  return { content: [{ type: 'text', text: `Tapped: ${selector}` }] };
}
```

Write `jst-mp-automator/src/tools/input.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_input';
export const description = 'Input text into a form field by CSS selector.';
export const inputSchema = z.object({
  selector: z.string().describe('CSS selector for input/textarea element'),
  value: z.string().describe('Text to input')
});

export async function handler({ selector, value }) {
  await bot.inputText(selector, value);
  return { content: [{ type: 'text', text: `Input "${value}" into ${selector}` }] };
}
```

Write `jst-mp-automator/src/tools/scroll.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_scroll';
export const description = 'Scroll the page up or down by a given distance in pixels.';
export const inputSchema = z.object({
  direction: z.enum(['up', 'down']).default('down'),
  distance: z.number().optional().default(500).describe('Scroll distance in pixels')
});

export async function handler({ direction, distance }) {
  await bot.scrollPage(direction, distance);
  return { content: [{ type: 'text', text: `Scrolled ${direction} ${distance}px` }] };
}
```

Write `jst-mp-automator/src/tools/screenshot.js`:

```javascript
import { z } from 'zod';
import fs from 'fs';
import path from 'path';
import * as bot from '../automator.js';
import config from '../config.js';
import { sleep } from '../automator.js';

export const name = 'mp_screenshot';
export const description = 'Take a screenshot of the current page. Use full_page=true to scroll and capture the entire page in multiple segments.';
export const inputSchema = z.object({
  full_page: z.boolean().optional().default(false).describe('Scroll and capture entire page in segments'),
  label: z.string().optional().describe('Label for the screenshot (used in filename)')
});

export async function handler({ full_page, label }) {
  const mp = bot.getMiniProgram();
  const page = await bot.getCurrentPage();
  const timestamp = Date.now();
  const prefix = label || page.path.replace(/\//g, '_');

  if (!full_page) {
    const base64 = await bot.screenshot();
    return {
      content: [
        { type: 'text', text: `Screenshot of ${page.path}` },
        { type: 'image', data: base64, mimeType: 'image/png' }
      ]
    };
  }

  // Full page: scroll and capture segments
  const pageSize = await page.size();
  const viewportHeight = pageSize.height || 812;

  // Get scroll height via evaluate
  let scrollHeight;
  try {
    scrollHeight = await mp.evaluate(() => {
      return document.documentElement.scrollHeight || document.body.scrollHeight;
    });
  } catch {
    // Fallback: just take one screenshot
    const base64 = await bot.screenshot();
    return {
      content: [
        { type: 'text', text: `Screenshot of ${page.path} (could not determine scroll height)` },
        { type: 'image', data: base64, mimeType: 'image/png' }
      ]
    };
  }

  const segments = Math.ceil(scrollHeight / viewportHeight);
  const content = [{ type: 'text', text: `Full page screenshot of ${page.path} (${segments} segments, height=${scrollHeight}px)` }];

  for (let i = 0; i < segments; i++) {
    await mp.pageScrollTo(i * viewportHeight);
    await sleep(500);
    const base64 = await bot.screenshot();
    content.push(
      { type: 'text', text: `[${i + 1}/${segments}] scroll position ${i * viewportHeight}px` },
      { type: 'image', data: base64, mimeType: 'image/png' }
    );
  }

  // Scroll back to top
  await mp.pageScrollTo(0);
  return { content };
}
```

Write `jst-mp-automator/src/tools/get-page-data.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_get_page_data';
export const description = 'Get the current page Vue component data object. Optionally pass a dot-path to get a specific value.';
export const inputSchema = z.object({
  path: z.string().optional().describe('Dot-path to specific data, e.g. "contestInfo.contestName" or "list[0].status"')
});

export async function handler({ path }) {
  const data = await bot.getPageData(path);
  return { content: [{ type: 'text', text: JSON.stringify(data, null, 2) }] };
}
```

Write `jst-mp-automator/src/tools/get-element.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_get_element';
export const description = 'Get element info by CSS selector: text content, size, position, existence.';
export const inputSchema = z.object({
  selector: z.string().describe('CSS selector')
});

export async function handler({ selector }) {
  const info = await bot.getElement(selector);
  if (!info) {
    return { content: [{ type: 'text', text: `Element not found: ${selector}` }] };
  }
  return { content: [{ type: 'text', text: JSON.stringify(info, null, 2) }] };
}
```

Write `jst-mp-automator/src/tools/get-console.js`:

```javascript
import { z } from 'zod';

export const name = 'mp_get_console';
export const description = 'Get captured console logs from the mini program. Currently returns a hint to check DevTools console.';
export const inputSchema = z.object({
  level: z.enum(['all', 'error', 'warn', 'log']).optional().default('all')
});

export async function handler({ level }) {
  // miniprogram-automator doesn't have a built-in console capture API.
  // Console logs are visible in DevTools directly.
  return {
    content: [{
      type: 'text',
      text: 'Console logs are available in WeChat DevTools console panel. Use mp_get_page_data or mp_screenshot to inspect current state.'
    }]
  };
}
```

Write `jst-mp-automator/src/tools/mock-login.js`:

```javascript
import { z } from 'zod';
import * as bot from '../automator.js';
import config from '../config.js';

export const name = 'mp_mock_login';
export const description = 'Login as a test user via mock API. Gets JWT token and injects into mini program storage. After login, navigates to my/index.';
export const inputSchema = z.object({
  mockCode: z.string().describe('Mock login code, e.g. MOCK_1001, MOCK_1003, MOCK_9001')
});

export async function handler({ mockCode }) {
  // 1. Call backend mock login API
  const resp = await fetch(`${config.backendUrl}/jst/wx/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code: mockCode })
  });
  const body = await resp.json();
  if (body.code !== 200) {
    return { content: [{ type: 'text', text: `Login failed: ${JSON.stringify(body)}` }] };
  }

  const { token, userInfo, roles } = body.data;

  // 2. Inject token into mini program storage
  const mp = bot.getMiniProgram();
  await mp.evaluate((t) => {
    wx.setStorageSync('jst-token', t);
  }, token);

  // 3. Navigate to my/index to trigger logged-in state
  await bot.reLaunchTo('/pages/my/index');

  return {
    content: [{
      type: 'text',
      text: `Logged in as ${mockCode}\nUser: ${userInfo.nickname} (${userInfo.userType})\nRoles: ${roles.map(r => r.roleKey || r).join(', ')}\nToken injected into storage.`
    }]
  };
}
```

Write `jst-mp-automator/src/tools/query-db.js`:

```javascript
import { z } from 'zod';
import { query } from '../db.js';

export const name = 'mp_query_db';
export const description = 'Execute a SELECT query against the test database (59.110.53.165/jst_new). Only SELECT allowed.';
export const inputSchema = z.object({
  sql: z.string().describe('SELECT SQL query')
});

export async function handler({ sql }) {
  const rows = await query(sql);
  return {
    content: [{
      type: 'text',
      text: `${rows.length} rows returned:\n${JSON.stringify(rows, null, 2)}`
    }]
  };
}
```

Write `jst-mp-automator/src/tools/exec-db.js`:

```javascript
import { z } from 'zod';
import { exec } from '../db.js';

export const name = 'mp_exec_db';
export const description = 'Execute INSERT/UPDATE/DELETE against the test database. All writes are logged. Use create_by="automator" for test data isolation.';
export const inputSchema = z.object({
  sql: z.string().describe('INSERT, UPDATE, or DELETE SQL statement')
});

export async function handler({ sql }) {
  const result = await exec(sql);
  return {
    content: [{
      type: 'text',
      text: `Executed. Affected rows: ${result.affectedRows}${result.insertId ? `, insertId: ${result.insertId}` : ''}`
    }]
  };
}
```

Write `jst-mp-automator/src/tools/run-flow.js` (stub — flow engine not yet implemented):

```javascript
import { z } from 'zod';

export const name = 'mp_run_flow';
export const description = 'Execute a predefined YAML test flow by name. Returns step-by-step results with screenshots.';
export const inputSchema = z.object({
  flowName: z.string().describe('Flow name without .yaml extension, e.g. "04-browse-home"'),
  params: z.record(z.string()).optional().describe('Override parameters for the flow')
});

export async function handler({ flowName, params }) {
  // Will be implemented in Task 6
  return { content: [{ type: 'text', text: `Flow engine not yet implemented. Flow: ${flowName}` }] };
}
```

Write `jst-mp-automator/src/tools/run-flow-list.js` (stub):

```javascript
import { z } from 'zod';
import fs from 'fs';
import path from 'path';
import config from '../config.js';

export const name = 'mp_run_flow_list';
export const description = 'List all available YAML test flows with their descriptions.';
export const inputSchema = z.object({});

export async function handler() {
  const flowsDir = config.flowsDir;
  let files = [];
  try {
    files = fs.readdirSync(flowsDir).filter(f => f.endsWith('.yaml') && !f.startsWith('_'));
  } catch {
    return { content: [{ type: 'text', text: `No flows directory found at ${flowsDir}` }] };
  }
  const list = files.map(f => `- ${f.replace('.yaml', '')}`).join('\n');
  return { content: [{ type: 'text', text: `Available flows (${files.length}):\n${list}` }] };
}
```

- [ ] **Step 2: Write MCP server entry index.js**

Write `jst-mp-automator/index.js`:

```javascript
#!/usr/bin/env node
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';

// Import all tools
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
  launchTool, navigateTool, tapTool, inputTool, scrollTool,
  screenshotTool, getPageDataTool, getElementTool, getConsoleTool,
  mockLoginTool, queryDbTool, execDbTool, runFlowTool, runFlowListTool
];

const server = new McpServer({
  name: 'jst-mp-automator',
  version: '1.0.0',
});

// Register all tools
for (const tool of tools) {
  server.tool(tool.name, tool.description, tool.inputSchema.shape, async (args) => {
    try {
      return await tool.handler(args);
    } catch (err) {
      console.error(`[${tool.name}] error:`, err.message);
      return { content: [{ type: 'text', text: `Error: ${err.message}` }], isError: true };
    }
  });
}

const transport = new StdioServerTransport();
await server.connect(transport);
console.error('[jst-mp-automator] MCP server started (stdio)');
```

- [ ] **Step 3: Verify server starts without crash**

```bash
cd D:/coding/jst_v1/jst-mp-automator && echo '{}' | timeout 3 node index.js 2>&1 || true
```

Expected: Server starts, prints `[jst-mp-automator] MCP server started (stdio)` to stderr, then exits on stdin close.

- [ ] **Step 4: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/index.js jst-mp-automator/src/tools/
git commit -m "feat(automator): MCP server entry + 14 tool registrations"
```

---

## Task 5: MCP Registration + Smoke Test

**Files:**
- Modify: `D:\coding\jst_v1\.mcp.json` (create if not exists)

- [ ] **Step 1: Create .mcp.json**

Write `D:\coding\jst_v1\.mcp.json`:

```json
{
  "mcpServers": {
    "jst-mp-automator": {
      "command": "node",
      "args": ["D:/coding/jst_v1/jst-mp-automator/index.js"],
      "env": {
        "DB_HOST": "59.110.53.165",
        "DB_PORT": "3306",
        "DB_NAME": "jst_new",
        "DB_USER": "jst_new",
        "DB_PASS": "J8zZpAa4zG8y6a7e",
        "BACKEND_URL": "http://127.0.0.1:8080",
        "PROJECT_PATH": "D:/coding/jst_v1/jst-uniapp/unpackage/dist/dev/mp-weixin"
      }
    }
  }
}
```

- [ ] **Step 2: Test with MCP Inspector**

```bash
cd D:/coding/jst_v1/jst-mp-automator && npx @modelcontextprotocol/inspector node index.js
```

In the inspector UI:
1. Verify 14 tools are listed
2. Try `mp_run_flow_list` — should return "No flows directory" or empty list
3. Try `mp_query_db` with `SELECT COUNT(*) as cnt FROM jst_user LIMIT 1` — should return a number

- [ ] **Step 3: Commit**

```bash
cd D:/coding/jst_v1
git add .mcp.json
git commit -m "feat(automator): register MCP server in .mcp.json"
```

---

## Task 6: Flow Engine

**Files:**
- Create: `jst-mp-automator/src/flow-engine.js`
- Create: `jst-mp-automator/src/reporter.js`
- Modify: `jst-mp-automator/src/tools/run-flow.js`

- [ ] **Step 1: Write flow-engine.js**

```javascript
import fs from 'fs';
import path from 'path';
import yaml from 'js-yaml';
import * as bot from './automator.js';
import { query, exec } from './db.js';
import config from './config.js';
import { sleep } from './automator.js';

export async function loadFlow(flowName) {
  const filePath = path.join(config.flowsDir, `${flowName}.yaml`);
  if (!fs.existsSync(filePath)) throw new Error(`Flow not found: ${filePath}`);
  const raw = fs.readFileSync(filePath, 'utf-8');
  return yaml.load(raw);
}

export async function runFlow(flow, params = {}) {
  const report = {
    flow: flow.name,
    description: flow.description,
    login: flow.login,
    timestamp: new Date().toISOString(),
    startTime: Date.now(),
    steps: [],
    summary: { total: 0, passed: 0, failed: 0, skipped: 0, productIssues: [] }
  };

  // 1. Login
  if (flow.login) {
    try {
      const resp = await fetch(`${config.backendUrl}/jst/wx/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code: flow.login })
      });
      const body = await resp.json();
      if (body.code === 200) {
        const mp = bot.getMiniProgram();
        await mp.evaluate((t) => { wx.setStorageSync('jst-token', t); }, body.data.token);
        await bot.reLaunchTo('/pages/my/index');
        await sleep(1000);
      }
    } catch (err) {
      console.error(`[flow] login failed: ${err.message}`);
    }
  }

  // 2. Preconditions
  if (flow.precondition) {
    for (const pre of flow.precondition) {
      try {
        const rows = await query(pre.sql);
        const row = rows[0] || {};
        const pass = evaluateExpect(row, pre.expect);
        if (!pass && pre.fix_sql) {
          console.error(`[flow] precondition failed, running fix_sql`);
          await exec(pre.fix_sql);
        }
      } catch (err) {
        console.error(`[flow] precondition error: ${err.message}`);
      }
    }
  }

  // 3. Steps
  for (let i = 0; i < (flow.steps || []).length; i++) {
    const step = flow.steps[i];
    const stepResult = {
      index: i + 1,
      name: step.name,
      status: 'pending',
      screenshots: [],
      assertions: [],
      productReview: step.check?.find(c => c.product_review)?.product_review || [],
      error: null,
    };

    try {
      // Execute action
      switch (step.action) {
        case 'navigate':
          await bot.navigateTo(step.path, step.query);
          break;
        case 'reLaunch':
          await bot.reLaunchTo(step.path);
          break;
        case 'tap':
          await bot.tapElement(step.selector);
          break;
        case 'input':
          await bot.inputText(step.selector, step.value);
          break;
        case 'scroll':
          await bot.scrollPage(step.direction || 'down', step.distance || 500);
          break;
        case 'wait':
          await sleep(step.wait || 1000);
          break;
        case 'login':
          // Login handled separately above; re-login if needed
          break;
      }

      // Wait after action
      if (step.wait && step.action !== 'wait') {
        await sleep(step.wait);
      }

      // Run checks
      if (step.check) {
        for (const check of step.check) {
          if (check.screenshot) {
            const base64 = await bot.screenshot();
            stepResult.screenshots.push(base64);
          }
          if (check.element) {
            const info = await bot.getElement(check.element);
            const pass = check.expect === 'exists' ? !!info : !info;
            stepResult.assertions.push({ type: 'element', selector: check.element, expect: check.expect, result: pass ? 'pass' : 'fail' });
          }
          if (check.page_data) {
            const val = await bot.getPageData(check.page_data);
            const pass = check.expect === 'not_empty' ? val !== null && val !== undefined && val !== '' : true;
            stepResult.assertions.push({ type: 'page_data', path: check.page_data, result: pass ? 'pass' : 'fail', actual: val });
          }
          if (check.db) {
            try {
              const rows = await query(check.db);
              const row = rows[0] || {};
              const pass = evaluateExpect(row, check.expect);
              stepResult.assertions.push({ type: 'db', sql: check.db, expect: check.expect, result: pass ? 'pass' : 'fail', actual: row });
            } catch (err) {
              stepResult.assertions.push({ type: 'db', sql: check.db, result: 'fail', error: err.message });
            }
          }
        }
      }

      const allAssertionsPass = stepResult.assertions.every(a => a.result === 'pass');
      stepResult.status = allAssertionsPass ? 'pass' : 'fail';
    } catch (err) {
      stepResult.status = 'fail';
      stepResult.error = err.message;
      // Take error screenshot
      try {
        const base64 = await bot.screenshot();
        stepResult.screenshots.push(base64);
      } catch {}
    }

    report.steps.push(stepResult);
    report.summary.total++;
    if (stepResult.status === 'pass') report.summary.passed++;
    else report.summary.failed++;

    // on_fail handling
    if (stepResult.status === 'fail' && step.on_fail === 'abort') break;
  }

  report.duration_ms = Date.now() - report.startTime;
  delete report.startTime;
  return report;
}

function evaluateExpect(row, expectStr) {
  if (!expectStr) return true;
  // Simple expression evaluator: "cnt >= 1", "status = 'paid'"
  const match = expectStr.match(/^(\w+)\s*(>=|<=|>|<|=|!=)\s*(.+)$/);
  if (!match) return true;
  const [, field, op, rawVal] = match;
  const actual = row[field];
  const expected = rawVal.replace(/^'|'$/g, '');
  switch (op) {
    case '>=': return Number(actual) >= Number(expected);
    case '<=': return Number(actual) <= Number(expected);
    case '>': return Number(actual) > Number(expected);
    case '<': return Number(actual) < Number(expected);
    case '=': return String(actual) === expected;
    case '!=': return String(actual) !== expected;
    default: return true;
  }
}
```

- [ ] **Step 2: Write reporter.js**

```javascript
import fs from 'fs';
import path from 'path';
import config from './config.js';

export function generateTextReport(report) {
  const lines = [];
  lines.push(`━━ ${report.flow} ━━`);
  lines.push(`${report.description}`);
  lines.push(`Login: ${report.login || 'none'} | Duration: ${(report.duration_ms / 1000).toFixed(1)}s`);
  lines.push(`Result: ${report.summary.passed}/${report.summary.total} steps passed`);
  lines.push('');

  for (const step of report.steps) {
    const icon = step.status === 'pass' ? '✅' : '❌';
    lines.push(`  ${icon} Step ${step.index}: ${step.name}`);
    if (step.error) lines.push(`     Error: ${step.error}`);
    for (const a of step.assertions) {
      const aIcon = a.result === 'pass' ? '✓' : '✗';
      lines.push(`     ${aIcon} [${a.type}] ${a.selector || a.path || a.sql?.substring(0, 60) || ''}`);
    }
    if (step.productReview.length > 0) {
      lines.push(`     📋 Product review questions (${step.productReview.length}):`);
      for (const q of step.productReview) {
        lines.push(`        • ${q}`);
      }
    }
  }

  return lines.join('\n');
}

export function saveReport(report, flowName) {
  const dir = config.reportsDir;
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
  const filename = `${flowName}-${Date.now()}.json`;
  const filePath = path.join(dir, filename);
  fs.writeFileSync(filePath, JSON.stringify(report, null, 2));
  return filePath;
}
```

- [ ] **Step 3: Update run-flow.js to use flow engine**

Replace `jst-mp-automator/src/tools/run-flow.js`:

```javascript
import { z } from 'zod';
import { loadFlow, runFlow } from '../flow-engine.js';
import { generateTextReport, saveReport } from '../reporter.js';

export const name = 'mp_run_flow';
export const description = 'Execute a predefined YAML test flow by name. Returns step-by-step results with screenshots and product review questions.';
export const inputSchema = z.object({
  flowName: z.string().describe('Flow name without .yaml extension, e.g. "04-browse-home"'),
  params: z.record(z.string()).optional().describe('Override parameters for the flow')
});

export async function handler({ flowName, params }) {
  const flow = await loadFlow(flowName);
  const report = await runFlow(flow, params);

  // Save report
  const reportPath = saveReport(report, flowName);

  // Build response with text report + screenshots from failed/interesting steps
  const content = [];
  content.push({ type: 'text', text: generateTextReport(report) });
  content.push({ type: 'text', text: `\nReport saved: ${reportPath}` });

  // Include screenshots for steps that have product_review questions or failed
  for (const step of report.steps) {
    if (step.screenshots.length > 0 && (step.status === 'fail' || step.productReview.length > 0)) {
      content.push({ type: 'text', text: `\n--- Step ${step.index}: ${step.name} (${step.status}) ---` });
      for (const base64 of step.screenshots) {
        content.push({ type: 'image', data: base64, mimeType: 'image/png' });
      }
      if (step.productReview.length > 0) {
        content.push({
          type: 'text',
          text: `Product review questions:\n${step.productReview.map(q => `  • ${q}`).join('\n')}\n\nPlease analyze the screenshot above from a senior product manager perspective and answer these questions.`
        });
      }
    }
  }

  return { content };
}
```

- [ ] **Step 4: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/src/flow-engine.js jst-mp-automator/src/reporter.js jst-mp-automator/src/tools/run-flow.js
git commit -m "feat(automator): YAML flow engine + reporter + run-flow tool"
```

---

## Task 7: First YAML Flows (01-09: Login + Browse)

**Files:**
- Create: `jst-mp-automator/flows/01-login.yaml` through `09-browse-notice.yaml`

These flows cover login, identity, and all browse pages. Each flow includes `product_review` questions that Claude will answer when analyzing screenshots.

See `docs/superpowers/specs/2026-04-15-mp-automator-design.md` section 六 for the full description of each flow.

**Page paths reference (from pages.json):**
- Tab pages: `/pages/index/index`, `/pages/contest/list`, `/pages/course/list`, `/pages/notice/list`, `/pages/my/index`
- Login: `/pages/login/login`
- Sub pages use: `/pages-sub/<module>/<page>`

**CSS selectors follow BEM:** `.page-name__element--modifier`
- Home: `.home-page__search-input`, `.home-page__hero`
- Contest: `.contest-list-page__search-input`, `.contest-list-page__cat-tab`
- My: `.my-page__switcher-item`, `.my-page__summary-item`

- [ ] **Step 1: Write 01-login.yaml**

```yaml
name: 登录流程
description: 登录页展示 → Mock登录 → 跳转个人中心
login: null

steps:
  - name: 进入登录页
    action: reLaunch
    path: /pages/login/login
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "登录页品牌形象是否清晰？用户3秒内能否理解这是什么应用？"
          - "登录按钮（微信快捷登录）是否醒目、位置是否合理？"
          - "是否有隐私协议提示和勾选？"

  - name: 执行Mock登录
    action: wait
    wait: 1000
    check:
      - screenshot: true
      - product_review:
          - "开发模式下的Mock登录按钮是否只在开发环境显示？"
```

- [ ] **Step 2: Write 02-role-switch.yaml**

```yaml
name: 角色切换
description: 个人中心学生↔渠道方视角切换 → 菜单变化 → 数据隔离验证
login: MOCK_1003

steps:
  - name: 进入个人中心（渠道身份）
    action: navigate
    path: /pages/my/index
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - element: ".my-page__switcher"
        expect: exists
      - product_review:
          - "角色切换器是否直观？用户能否一眼看出当前是哪个身份？"
          - "渠道方视角下的菜单项是否与学生视角明显不同？"

  - name: 切换到学生视角
    action: tap
    selector: ".my-page__switcher-item:first-child"
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "切换后页面是否立即刷新？菜单是否变为学生专属？"
          - "切换过程是否有过渡动画或加载提示？"
          - "学生视角下是否完全看不到渠道方功能入口？"

  - name: 切换回渠道方视角
    action: tap
    selector: ".my-page__switcher-item:last-child"
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "反复切换是否流畅？数据是否隔离正确？"
```

- [ ] **Step 3: Write 03-bind-channel.yaml**

```yaml
name: 绑定渠道方
description: 学生扫渠道方二维码/链接 → 确认绑定 → 查看绑定 → 解绑
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_channel WHERE status='approved' AND del_flag='0'"
    expect: "cnt >= 1"

steps:
  - name: 进入绑定页面
    action: navigate
    path: /pages-sub/my/binding
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "绑定页是否清楚说明什么是渠道绑定、绑定后有什么变化？"
          - "当前绑定状态（已绑定/未绑定）是否醒目展示？"
          - "如果已绑定，是否显示渠道方名称和绑定时间？"
```

- [ ] **Step 4: Write 04-browse-home.yaml**

```yaml
name: 首页浏览
description: 首页完整浏览 — Banner/公告条/宫格跳转/推荐赛事/推荐课程
login: MOCK_1001

steps:
  - name: 进入首页
    action: navigate
    path: /pages/index/index
    wait: 3000
    check:
      - screenshot: { full_page: true }
      - element: ".home-page__hero"
        expect: exists
      - product_review:
          - "首页打开后3秒内，用户能否理解平台功能？核心入口是否醒目？"
          - "Banner轮播是否加载完成？空白时是否有骨架屏？"
          - "公告条是否滚动展示？内容是否有意义？"
          - "宫格入口（积分商城/优惠券/权益等）图标和文案是否清晰？"

  - name: 检查推荐赛事区域
    action: scroll
    direction: down
    distance: 600
    wait: 1000
    check:
      - screenshot: true
      - product_review:
          - "推荐赛事卡片展示了哪些信息？是否包含价格、报名人数、截止时间？"
          - "赛事卡片点击热区是否够大？"
          - "如果没有推荐赛事，是否有空状态提示？"

  - name: 检查推荐课程区域
    action: scroll
    direction: down
    distance: 600
    wait: 1000
    check:
      - screenshot: true
      - product_review:
          - "推荐课程信息是否充足（封面/标题/讲师/价格）？"
          - "页面底部是否有明确的结束标识？不会让用户以为还有更多内容？"
```

- [ ] **Step 5: Write 05-global-search.yaml**

```yaml
name: 全局搜索
description: 搜索赛事/课程 → 搜索历史 → 热门搜索 → 清空
login: MOCK_1001

steps:
  - name: 点击搜索栏
    action: tap
    selector: ".home-page__search"
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "搜索页是否有热门搜索词推荐？"
          - "是否展示搜索历史？有清空历史按钮吗？"
          - "输入框是否自动聚焦？键盘是否弹出？"

  - name: 输入搜索关键词
    action: input
    selector: ".contest-list-page__search-input"
    value: "测试"
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "搜索结果是否即时展示（边输边搜）还是需要按回车？"
          - "搜索结果卡片信息是否与列表页一致？"
          - "无结果时是否有友好提示？"
```

- [ ] **Step 6: Write 06 through 09**

Write `06-browse-contest.yaml`:

```yaml
name: 赛事列表浏览与筛选
description: 赛事列表 → 分类Tab → 多级筛选 → 赛事卡片信息验证
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_contest WHERE status='enrolling' AND del_flag='0'"
    expect: "cnt >= 1"

steps:
  - name: 进入赛事列表
    action: navigate
    path: /pages/contest/list
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "赛事列表页首次加载是否有骨架屏？加载完成过渡是否自然？"
          - "分类Tab是否清晰可见？当前选中Tab有无高亮？"
          - "赛事卡片展示了哪些信息？是否包含：名称/封面/价格/报名时间/报名人数？"
          - "用户能否快速判断哪些赛事可以报名？"

  - name: 点击分类Tab筛选
    action: tap
    selector: ".contest-list-page__cat-tab:nth-child(2)"
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "切换分类后列表是否刷新？是否有加载动画？"
          - "选中的Tab样式是否有明显区分？"

  - name: 使用搜索
    action: tap
    selector: ".contest-list-page__search-box"
    wait: 1000
    check:
      - screenshot: true
```

Write `07-contest-detail.yaml`:

```yaml
name: 赛事详情
description: 赛事详情页完整浏览 — 基本信息/赛程/奖项/FAQ/报名按钮状态
login: MOCK_1001

steps:
  - name: 从列表进入详情
    action: navigate
    path: /pages-sub/contest/detail
    query:
      id: "8201"
    wait: 3000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "用户3秒内能否理解：这是什么赛事、多少钱、什么时候报名截止？"
          - "Banner/封面图加载是否正常？"
          - "报名按钮是否固定在底部？颜色是否醒目？"
          - "赛事状态（报名中/已截止/已结束）是否有明确标识？"
          - "如果赛事已截止或名额已满，按钮是否禁用并说明原因？"
          - "页面信息组织层次：基本信息 → 时间 → 费用 → 赛程 → 奖项 → FAQ，是否清晰？"
          - "FAQ是否可展开收起？交互是否顺畅？"
          - "分享按钮是否可见？位置是否合理？"
```

Write `08-browse-course.yaml`:

```yaml
name: 课程列表浏览
description: 课程列表 → 搜索 → 类型筛选（普通/AI）→ 课程卡片信息
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_course WHERE status='published' AND del_flag='0'"
    expect: "cnt >= 1"

steps:
  - name: 进入课程列表
    action: navigate
    path: /pages/course/list
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "课程卡片展示了哪些信息？是否包含：封面/名称/讲师/价格/学习人数？"
          - "普通课程和AI课程的区分是否清晰？"
          - "空列表时是否有引导？"
```

Write `09-browse-notice.yaml`:

```yaml
name: 公告浏览
description: 公告列表 → 公告详情 → 富文本展示
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_notice WHERE status='published' AND del_flag='0'"
    expect: "cnt >= 1"

steps:
  - name: 进入公告列表
    action: navigate
    path: /pages/notice/list
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "公告卡片展示了哪些信息？是否包含标题/摘要/发布时间/分类？"
          - "列表是否支持搜索？下拉是否可以加载更多？"
          - "空列表时是否有提示？"
```

- [ ] **Step 7: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/01-login.yaml jst-mp-automator/flows/02-role-switch.yaml jst-mp-automator/flows/03-bind-channel.yaml jst-mp-automator/flows/04-browse-home.yaml jst-mp-automator/flows/05-global-search.yaml jst-mp-automator/flows/06-browse-contest.yaml jst-mp-automator/flows/07-contest-detail.yaml jst-mp-automator/flows/08-browse-course.yaml jst-mp-automator/flows/09-browse-notice.yaml
git commit -m "feat(automator): YAML flows 01-09 (login + browse)"
```

---

## Task 8: Transaction Flows (10-17)

**Files:**
- Create: `jst-mp-automator/flows/10-enroll-pay.yaml` through `17-my-course.yaml`

These are the core transaction and course flows. Each YAML follows the same schema as Task 7. The implementer should:

1. Read the spec (section 六) for each flow's description
2. Use the page paths from pages.json (listed in Task 7 header)
3. Include `precondition` SQL checks for required test data
4. Include `product_review` questions from the **product experience audit framework** (spec section 五)

- [ ] **Step 1: Write 10-enroll-pay.yaml**

This is the most complex flow — enrollment with dynamic form, draft save, payment.

```yaml
name: 报名支付全链路
description: 报名 → 选参赛人 → 动态表单 → 草稿保存 → 恢复 → 提交 → 优惠券 → 积分抵扣 → 支付
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_contest WHERE status='enrolling' AND del_flag='0' AND price > 0"
    expect: "cnt >= 1"
  - sql: "SELECT COUNT(*) as cnt FROM jst_participant WHERE user_id=1001 AND del_flag='0'"
    expect: "cnt >= 1"
    fix_sql: "INSERT INTO jst_participant (user_id, real_name, id_card, gender, school, grade, create_by, create_time, del_flag) VALUES (1001, '测试_小明', '110101200801011234', 'M', '测试小学', '三年级', 'automator', NOW(), '0')"

steps:
  - name: 进入报名页
    action: navigate
    path: /pages-sub/contest/enroll
    query:
      contestId: "8201"
    wait: 3000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "报名流程是否有步骤指示器？用户知道总共几步吗？"
          - "参赛人选择是否直观？没有档案时是否引导创建？"
          - "动态表单字段是否根据赛事配置正确渲染？"
          - "必填项是否有星号标记？"

  - name: 选择参赛人
    action: tap
    selector: ".participant-card:first-child"
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "选中参赛人后是否有视觉反馈（高亮/勾选）？"
          - "参赛人信息是否展示完整（姓名/学校/年级）？"

  - name: 检查表单区域
    action: scroll
    direction: down
    distance: 500
    wait: 1000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "表单字段标签是否清晰？输入框占位符是否有引导作用？"
          - "文件上传区域是否有格式和大小说明？"
          - "实时校验是否工作？填错时是否立即提示？"

  - name: 检查底部支付区域
    action: scroll
    direction: down
    distance: 1000
    wait: 1000
    check:
      - screenshot: true
      - product_review:
          - "价格展示是否清晰？原价/优惠/实付是否分行展示？"
          - "优惠券模块：有可用券时是否自动推荐？无券时是否隐藏？"
          - "积分抵扣：是否显示可用积分和可抵扣金额？"
          - "提交按钮文案是否明确（'提交报名'而非'提交'）？"
```

- [ ] **Step 2: Write remaining flows 11-17**

Each flow follows the same YAML pattern. Key points for each:

**11-enroll-zero.yaml**: Navigate to a zero-price contest, verify no payment step, verify no points/rebate awarded. Use `db` check to verify `price=0` handling.

**12-my-enroll.yaml**: Navigate to `/pages-sub/my/enroll`, check tab switching (草稿/待审/已通过/已驳回), verify card info, test draft editing.

**13-order-manage.yaml**: Navigate to `/pages-sub/my/order-list`, check status tabs, enter order detail at `/pages-sub/my/order-detail`, verify amount display.

**14-refund-apply.yaml**: From order detail, tap refund button, fill reason, submit, verify DB status change.

**15-refund-list.yaml**: Navigate to `/pages-sub/my/refund-list`, check status tabs, enter refund detail.

**16-course-detail.yaml**: Navigate to `/pages-sub/course/detail?id=xxx`, check teacher info, chapter list, free/paid markers.

**17-my-course.yaml**: Navigate to `/pages-sub/my/course`, check progress display, continue learning button.

The implementer writes each file following the established pattern, using the spec's product_review framework (section 五) to generate review questions appropriate to each page's function.

- [ ] **Step 3: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/1*.yaml
git commit -m "feat(automator): YAML flows 10-17 (transactions + courses)"
```

---

## Task 9: Personal Center Flows (18-26)

**Files:**
- Create: `jst-mp-automator/flows/18-my-index.yaml` through `26-help-support.yaml`

Key page paths for this batch:
- `/pages/my/index` (个人中心首页)
- `/pages-sub/my/profile-edit` (资料编辑)
- `/pages-sub/my/participant` (参赛人档案)
- `/pages-sub/my/address-list`, `/pages-sub/my/address-edit` (地址)
- `/pages-sub/my/binding` (绑定关系)
- `/pages-sub/my/score`, `/pages-sub/my/cert` (成绩证书)
- `/pages-sub/my/score-detail`, `/pages-sub/my/cert-detail`
- `/pages-sub/my/message` (消息中心)
- `/pages-sub/my/settings` (设置)
- `/pages-sub/public/help` (客服帮助)

- [ ] **Step 1: Write all 9 flows**

Each flow follows the established YAML pattern with `product_review` questions tailored to the specific page function. The implementer reads the spec section 六 for each flow's description and uses section 五's audit framework.

Key product_review focuses for this batch:
- **18-my-index**: All entry points must be present and tappable; badge counts visible; role switcher state
- **19-profile-edit**: Form pre-fill with current data; save feedback; avatar upload
- **20-participant-manage**: CRUD operations; empty state; delete confirmation
- **21-address-manage**: Form validation; default address marking; delete safety
- **22-binding-manage**: Current binding display; unbind confirmation dialog
- **23-score-cert**: Radar chart rendering; certificate canvas; save-to-album button
- **24-message-center**: Message type tabs; unread indicators; tap to navigate to source
- **25-settings**: Toggle switches work; privacy policy link; logout confirmation
- **26-help-support**: FAQ expandable; contact info; search FAQ

- [ ] **Step 2: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/1[89]*.yaml jst-mp-automator/flows/2[0-6]*.yaml
git commit -m "feat(automator): YAML flows 18-26 (personal center)"
```

---

## Task 10: Points + Marketing + Appointment Flows (27-32)

**Files:**
- Create: `jst-mp-automator/flows/27-points-center.yaml` through `32-writeoff-station.yaml`

Key page paths:
- `/pages-sub/points/center`, `/pages-sub/points/detail` (积分)
- `/pages-sub/mall/list`, `/pages-sub/mall/detail`, `/pages-sub/mall/exchange-list` (商城)
- `/pages-sub/coupon/center`, `/pages-sub/coupon/claimable`, `/pages-sub/coupon/select` (优惠券)
- `/pages-sub/rights/center`, `/pages-sub/rights/detail`, `/pages-sub/rights/writeoff-apply` (权益)
- `/pages-sub/appointment/apply`, `/pages-sub/appointment/my-list`, `/pages-sub/appointment/detail` (预约)
- `/pages-sub/appointment/scan`, `/pages-sub/appointment/writeoff-record` (核销)

- [ ] **Step 1: Write all 6 flows**

- [ ] **Step 2: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/2[7-9]*.yaml jst-mp-automator/flows/3[0-2]*.yaml
git commit -m "feat(automator): YAML flows 27-32 (points + marketing + appointment)"
```

---

## Task 11: Channel Flows (33-42)

**Files:**
- Create: `jst-mp-automator/flows/33-channel-auth.yaml` through `42-contract-invoice.yaml`

Key page paths (all under `/pages-sub/channel/`):
- `apply-entry`, `apply-form`, `apply-status` (认证)
- `home` (工作台首页)
- (QR code generation — may be in home page)
- `students`, `student-score`, `student-cert` (学生管理)
- `orders`, `order-detail` (订单)
- `rebate`, `withdraw-apply`, `withdraw-list`, `withdraw-detail` (返点提现)
- `batch-enroll`, `participants` (批量报名)
- `appointment` (团队预约)
- `data` (经营分析)
- `contract-list`, `contract-detail`, `invoice-list`, `invoice-apply` (合同开票)
- `settlement` (结算)

All channel flows use `login: MOCK_1003` (渠道身份).

- [ ] **Step 1: Write all 10 flows**

- [ ] **Step 2: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/3[3-9]*.yaml jst-mp-automator/flows/4[0-2]*.yaml
git commit -m "feat(automator): YAML flows 33-42 (channel)"
```

---

## Task 12: Edge Cases + Guest + Regression (43-45)

**Files:**
- Create: `jst-mp-automator/flows/43-edge-cases.yaml`
- Create: `jst-mp-automator/flows/44-public-query.yaml`
- Create: `jst-mp-automator/flows/45-partner-apply.yaml`
- Create: `jst-mp-automator/flows/_full-regression.yaml`

- [ ] **Step 1: Write 43-edge-cases.yaml**

```yaml
name: 边界场景合集
description: 已截止赛事/已满名额/空列表/未登录拦截/支付失败重试
login: MOCK_1001

steps:
  - name: 查看已截止赛事
    action: navigate
    path: /pages-sub/contest/detail
    query:
      id: "8202"
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "已截止赛事的报名按钮是否禁用？是否显示'报名已截止'文案？"
          - "已截止赛事是否仍展示完整信息（赛程/奖项/FAQ）？"
          - "是否显示最终报名人数和结果（如已公布）？"

  - name: 访问空列表页面
    action: navigate
    path: /pages-sub/my/enroll
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "没有数据时是否显示空状态插图和文案？"
          - "空状态是否有引导操作按钮（如'去报名'）？"
          - "空状态文案是否友好（不是'暂无数据'这种技术语言）？"

  - name: 未登录访问受限页面
    action: wait
    wait: 1000
    check:
      - screenshot: true
      - product_review:
          - "未登录状态下访问需要登录的功能时，是否有友好提示？"
          - "是否自动弹出登录引导？登录后是否自动返回原页面？"
```

- [ ] **Step 2: Write 44-public-query.yaml and 45-partner-apply.yaml**

```yaml
# 44-public-query.yaml
name: 公开查询
description: 成绩查询（无需登录）+ 证书验真（无需登录）
login: null

steps:
  - name: 进入成绩查询页
    action: reLaunch
    path: /pages-sub/public/score-query
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "查询页是否说明需要输入什么信息？"
          - "输入框占位符是否有引导（如'请输入参赛人姓名'）？"
          - "查询按钮是否醒目？"

  - name: 进入证书验真页
    action: navigate
    path: /pages-sub/public/cert-verify
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "证书验真输入框是否说明证书编号格式？"
          - "验证结果展示是否清晰（真/假 + 详细信息）？"
```

```yaml
# 45-partner-apply.yaml
name: 赛事方入驻
description: 赛事方入驻申请（4步表单，无需登录）
login: null

steps:
  - name: 进入入驻申请页
    action: reLaunch
    path: /pages-sub/public/partner-apply
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "入驻流程是否有步骤指示（4步）？用户知道需要准备什么材料吗？"
          - "表单字段分组是否合理？必填项标记是否清晰？"
          - "文件上传区域是否说明支持的格式和大小？"
          - "是否支持草稿保存？"
```

- [ ] **Step 3: Write _full-regression.yaml**

```yaml
name: 全量回归测试
description: 按依赖顺序串联全部45条链路

sequence:
  # 登录与身份
  - flow: 01-login
  - flow: 03-bind-channel
  - flow: 02-role-switch

  # 浏览
  - flow: 04-browse-home
  - flow: 05-global-search
  - flow: 06-browse-contest
  - flow: 07-contest-detail
  - flow: 08-browse-course
  - flow: 09-browse-notice

  # 交易
  - flow: 10-enroll-pay
  - flow: 11-enroll-zero
  - flow: 12-my-enroll
  - flow: 13-order-manage
  - flow: 14-refund-apply
  - flow: 15-refund-list

  # 课程
  - flow: 16-course-detail
  - flow: 17-my-course

  # 个人中心
  - flow: 18-my-index
  - flow: 19-profile-edit
  - flow: 20-participant-manage
  - flow: 21-address-manage
  - flow: 22-binding-manage
  - flow: 23-score-cert
  - flow: 24-message-center
  - flow: 25-settings
  - flow: 26-help-support

  # 积分营销
  - flow: 27-points-center
  - flow: 28-points-mall
  - flow: 29-coupon
  - flow: 30-rights

  # 预约核销
  - flow: 31-appointment
  - flow: 32-writeoff-station

  # 渠道方 (切换账号)
  - flow: 33-channel-auth
  - flow: 34-channel-workspace
  - flow: 35-channel-qrcode
  - flow: 36-channel-student
  - flow: 37-channel-order
  - flow: 38-channel-rebate
  - flow: 39-channel-batch-enroll
  - flow: 40-team-booking
  - flow: 41-channel-analytics
  - flow: 42-contract-invoice

  # 边界与游客
  - flow: 43-edge-cases
  - flow: 44-public-query
  - flow: 45-partner-apply
```

- [ ] **Step 4: Commit**

```bash
cd D:/coding/jst_v1
git add jst-mp-automator/flows/
git commit -m "feat(automator): YAML flows 43-45 + full regression script"
```

---

## Task 13: End-to-End Validation

**Files:** None new — this is a verification task.

- [ ] **Step 1: Compile UniApp to mp-weixin**

```bash
cd D:/coding/jst_v1/jst-uniapp && npm run dev:mp-weixin
```

Wait for compilation to complete. Verify output at `unpackage/dist/dev/mp-weixin/`.

- [ ] **Step 2: Open project in WeChat DevTools**

Open WeChat DevTools manually, import project from `D:/coding/jst_v1/jst-uniapp/unpackage/dist/dev/mp-weixin`. Verify the mini program loads.

- [ ] **Step 3: Start backend**

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn spring-boot:run -pl ruoyi-admin -am -Dspring-boot.run.profiles=dev
```

Or use the restart script:

```bash
powershell -File D:/coding/jst_v1/scripts/restart-admin.ps1
```

Verify `http://127.0.0.1:8080` is responding.

- [ ] **Step 4: Restart Claude Code to pick up MCP server**

Exit and re-enter Claude Code so it reads `.mcp.json` and starts the MCP server.

- [ ] **Step 5: Run a single tool manually**

In Claude Code, ask: "Use mp_query_db to run: SELECT COUNT(*) as cnt FROM jst_user"

Expected: Returns a number.

- [ ] **Step 6: Run mp_launch**

In Claude Code, ask: "Use mp_launch to connect to DevTools"

Expected: "Connected. Current page: ..."

- [ ] **Step 7: Run a single flow**

In Claude Code, ask: "Use mp_run_flow with flowName '04-browse-home'"

Expected: Step-by-step results with screenshots and product review questions. Claude analyzes the screenshots and answers the review questions.

- [ ] **Step 8: Run full regression (when ready)**

In Claude Code, ask: "Run the full regression test"

This executes `_full-regression.yaml` sequentially. Expected: Complete report with pass/fail per flow, product experience scores, and issue list.

- [ ] **Step 9: Final commit**

After validating the end-to-end flow works, make any necessary fixes and commit:

```bash
cd D:/coding/jst_v1
git add -A jst-mp-automator/
git commit -m "feat(automator): end-to-end validation complete"
```

---

## Implementation Notes

### miniprogram-automator Port

The default WebSocket port is **9420**, which is different from the "service port" (24466) in DevTools settings. The service port enables the CLI HTTP API; the automator uses a separate WebSocket connection. When using `automator.launch()`, the port is handled automatically. If connecting to an already-running DevTools, set `WX_WS_ENDPOINT=ws://localhost:9420` or the correct port.

### CSS Selectors in UniApp

UniApp compiles Vue SFC to WeChat WXML. The CSS classes in `.vue` files are preserved in the compiled output. The project uses **BEM naming**: `.page-name__element--modifier`. For example:
- `.home-page__search-input`
- `.contest-list-page__cat-tab--active`
- `.my-page__switcher-item`

### YAML Flow Writing Guide

When writing new flows, follow this checklist:
1. **precondition**: Query DB to verify required test data exists; include `fix_sql` to auto-insert
2. **login**: Use the correct mock code for the role being tested
3. **wait**: Add generous waits (2000-3000ms) for pages that load data from API
4. **product_review**: Write questions from a user's perspective, not a developer's. Ask "can the user..." not "does the element render..."
5. **db checks**: Verify backend state matches UI state after write operations
6. **full_page screenshots**: Use for pages with scrollable content (detail pages, forms)

### Existing MCP Server Reference

The npm package `@creatoria/miniapp-mcp` is a production MCP server wrapping `miniprogram-automator` with 65+ tools. It can be used as API reference but does not include our custom features (YAML flows, DB integration, product review).
