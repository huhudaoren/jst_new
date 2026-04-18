import automator from 'miniprogram-automator';
import { spawn } from 'child_process';
import { writeFile } from 'fs/promises';
import net from 'net';
import config from './config.js';

const TAB_PAGES = [
  '/pages/index/index',
  '/pages/contest/list',
  '/pages/course/list',
  '/pages/notice/list',
  '/pages/my/index',
];

let miniProgram = null;
let cliProcess = null;

/**
 * Promise-based sleep
 */
export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Retry helper with exponential backoff and timeout.
 * Retries up to config.retryCount times.
 * Each attempt is bounded by config.actionTimeout via Promise.race.
 */
async function withRetry(fn, label = 'action') {
  let lastError;
  for (let i = 0; i < config.retryCount; i++) {
    try {
      const timeoutPromise = new Promise((_, reject) =>
        setTimeout(() => reject(new Error(`[${label}] timed out after ${config.actionTimeout}ms`)), config.actionTimeout)
      );
      const result = await Promise.race([fn(), timeoutPromise]);
      return result;
    } catch (err) {
      lastError = err;
      console.error(`[${label}] attempt ${i + 1}/${config.retryCount} failed: ${err.message}`);
      if (i < config.retryCount - 1) {
        const delay = config.retryBaseDelay * Math.pow(2, i);
        console.error(`[${label}] retrying in ${delay}ms...`);
        await sleep(delay);
      }
    }
  }
  throw lastError;
}

/**
 * Check if a TCP port is open.
 */
function isPortOpen(port, host = '127.0.0.1') {
  return new Promise(resolve => {
    const sock = new net.Socket();
    sock.setTimeout(1000);
    sock.once('connect', () => { sock.destroy(); resolve(true); });
    sock.once('error', () => { sock.destroy(); resolve(false); });
    sock.once('timeout', () => { sock.destroy(); resolve(false); });
    sock.connect(port, host);
  });
}

/**
 * Launch CLI auto command with shell:true (Node.js 24 .bat fix),
 * wait for WebSocket port, then connect via automator.connect().
 */
async function launchViaShell() {
  const port = config.automatorPort;
  console.error(`[automator] spawning CLI auto on port ${port} (shell mode)...`);

  cliProcess = spawn(
    config.devtoolsCliPath,
    ['auto', '--project', config.projectPath, '--auto-port', String(port)],
    { stdio: 'ignore', shell: true, detached: true }
  );
  cliProcess.unref();
  cliProcess.on('error', e => console.error(`[automator] cli error: ${e.message}`));

  // Wait up to 30s for the WebSocket port to open
  const deadline = Date.now() + 30000;
  while (Date.now() < deadline) {
    if (await isPortOpen(port)) {
      console.error(`[automator] port ${port} is open, connecting...`);
      await sleep(2000); // give it a moment to stabilize
      miniProgram = await automator.connect({ wsEndpoint: `ws://127.0.0.1:${port}` });
      console.error('[automator] connected successfully');
      return miniProgram;
    }
    await sleep(1000);
  }
  throw new Error(`Timed out waiting for WebSocket port ${port}`);
}

/**
 * Connect to or launch WeChat DevTools via miniprogram-automator.
 * If config.wsEndpoint is set, uses automator.connect().
 * Otherwise spawns CLI with shell:true (workaround for Node.js 24 + .bat)
 * then connects via WebSocket.
 */
export async function launch() {
  if (miniProgram) {
    console.error('[automator] already connected, reusing existing instance');
    return miniProgram;
  }

  if (config.wsEndpoint) {
    console.error(`[automator] connecting to existing DevTools at ${config.wsEndpoint}`);
    miniProgram = await automator.connect({
      wsEndpoint: config.wsEndpoint,
    });
    console.error('[automator] connected successfully');
    return miniProgram;
  }

  return await launchViaShell();
}

/**
 * Get the connected MiniProgram instance. Throws if not connected.
 */
export function getMiniProgram() {
  if (!miniProgram) {
    throw new Error('[automator] not connected. Call launch() first.');
  }
  return miniProgram;
}

/**
 * Navigate to a page. Auto-detects tab pages and uses switchTab for them.
 */
export async function navigateTo(path, query = {}) {
  const mp = getMiniProgram();

  // Build query string
  const qs = Object.entries(query)
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
    .join('&');
  const fullPath = qs ? `${path}?${qs}` : path;

  // Check if it is a tab page
  const isTab = TAB_PAGES.includes(path);

  await withRetry(async () => {
    if (isTab) {
      console.error(`[automator] switchTab -> ${path}`);
      await mp.switchTab(path);
    } else {
      console.error(`[automator] navigateTo -> ${fullPath}`);
      await mp.navigateTo(fullPath);
    }
  }, `navigateTo(${path})`);

  await sleep(config.waitAfterNavigate);
}

/**
 * reLaunch to clear the page stack.
 */
export async function reLaunchTo(path) {
  const mp = getMiniProgram();
  await withRetry(async () => {
    console.error(`[automator] reLaunch -> ${path}`);
    await mp.reLaunch(path);
  }, `reLaunchTo(${path})`);
  await sleep(config.waitAfterNavigate);
}

/**
 * Get the current page instance.
 */
export async function getCurrentPage() {
  const mp = getMiniProgram();
  return await withRetry(async () => {
    const page = await mp.currentPage();
    return page;
  }, 'getCurrentPage');
}

/**
 * Take a screenshot. Returns base64 string if no savePath provided.
 */
export async function screenshot(savePath) {
  const mp = getMiniProgram();
  await sleep(config.waitBeforeScreenshot);

  return await withRetry(async () => {
    if (savePath) {
      console.error(`[automator] screenshot -> ${savePath}`);
      await mp.screenshot({ path: savePath });
      return savePath;
    } else {
      const buf = await mp.screenshot();
      if (Buffer.isBuffer(buf)) {
        return buf.toString('base64');
      }
      return buf;
    }
  }, 'screenshot');
}

/**
 * Find an element with retry, then tap it.
 */
export async function tapElement(selector) {
  const mp = getMiniProgram();
  await withRetry(async () => {
    const page = await mp.currentPage();
    const el = await page.$(selector);
    if (!el) {
      throw new Error(`Element not found: ${selector}`);
    }
    console.error(`[automator] tap -> ${selector}`);
    await el.tap();
  }, `tapElement(${selector})`);
}

/**
 * Find an element with retry, then input text.
 */
export async function inputText(selector, value) {
  const mp = getMiniProgram();
  await withRetry(async () => {
    const page = await mp.currentPage();
    const el = await page.$(selector);
    if (!el) {
      throw new Error(`Element not found: ${selector}`);
    }
    console.error(`[automator] input -> ${selector}: "${value}"`);
    await el.input(value);
  }, `inputText(${selector})`);
}

/**
 * Scroll the page up or down by a specified distance.
 */
export async function scrollPage(direction = 'down', distance = 300) {
  const mp = getMiniProgram();
  await withRetry(async () => {
    const page = await mp.currentPage();
    const offset = direction === 'down' ? distance : -distance;
    console.error(`[automator] scroll ${direction} by ${distance}px`);
    await page.scrollTop(offset);
  }, `scrollPage(${direction})`);
}

/**
 * Get page data. If a dot-path is provided, traverse to that key.
 * E.g., getPageData('list.0.name') returns page.data.list[0].name
 */
export async function getPageData(path) {
  const mp = getMiniProgram();
  return await withRetry(async () => {
    const page = await mp.currentPage();
    const data = await page.data();

    if (!path) return data;

    // Traverse dot-path
    const keys = path.split('.');
    let result = data;
    for (const key of keys) {
      if (result == null) return undefined;
      result = result[key];
    }
    return result;
  }, `getPageData(${path || 'root'})`);
}

/**
 * Get element info: text, wxml, size, offset.
 */
export async function getElement(selector) {
  const mp = getMiniProgram();
  return await withRetry(async () => {
    const page = await mp.currentPage();
    const el = await page.$(selector);
    if (!el) {
      throw new Error(`Element not found: ${selector}`);
    }

    const [text, wxml, offset, size] = await Promise.all([
      el.text().catch(() => ''),
      el.wxml().catch(() => ''),
      el.offset().catch(() => null),
      el.size().catch(() => null),
    ]);

    return { text, wxml, offset, size };
  }, `getElement(${selector})`);
}

/**
 * Execute JS in the mini program context.
 */
export async function evaluate(fn, ...args) {
  const mp = getMiniProgram();
  return await withRetry(async () => {
    return await mp.evaluate(fn, ...args);
  }, 'evaluate');
}

/**
 * Call a wx API method (e.g., 'getStorageSync', 'showToast').
 */
export async function callWxMethod(method, ...args) {
  const mp = getMiniProgram();
  return await withRetry(async () => {
    console.error(`[automator] callWxMethod -> wx.${method}`);
    return await mp.callWxMethod(method, ...args);
  }, `callWxMethod(${method})`);
}

/**
 * Close the automator connection.
 */
export async function close() {
  if (miniProgram) {
    console.error('[automator] closing connection');
    try {
      await miniProgram.close();
    } catch (err) {
      console.error(`[automator] close error: ${err.message}`);
    }
    miniProgram = null;
  }
}
