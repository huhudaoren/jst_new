#!/usr/bin/env node

const fs = require("fs");
const fsp = require("fs/promises");
const os = require("os");
const path = require("path");
const { spawn } = require("child_process");
const { pathToFileURL } = require("url");

const ROOT_DIR = process.argv[2]
  ? path.resolve(process.argv[2])
  : path.resolve(__dirname, "..", "小程序原型图");

const MOBILE_VIEWPORT = {
  width: 390,
  height: 844,
  deviceScaleFactor: 2,
  mobile: true,
};

const DESKTOP_VIEWPORT = {
  width: 1440,
  height: 1200,
  deviceScaleFactor: 1,
  mobile: false,
};

const CHROME_CANDIDATES = [
  process.env.CHROME_PATH,
  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
  "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
  "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
].filter(Boolean);

const STARTUP_TIMEOUT_MS = 15000;
const NAVIGATION_TIMEOUT_MS = 15000;
const SETTLE_DELAY_MS = 600;
const POST_SCROLL_DELAY_MS = 350;

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function waitForProcessExit(processHandle, timeoutMs) {
  if (!processHandle || processHandle.exitCode !== null) {
    return;
  }

  await new Promise((resolve) => {
    let settled = false;

    const finish = () => {
      if (!settled) {
        settled = true;
        resolve();
      }
    };

    const timer = setTimeout(finish, timeoutMs);

    processHandle.once("exit", () => {
      clearTimeout(timer);
      finish();
    });
  });
}

async function stopBrowserProcess(processHandle) {
  if (!processHandle || processHandle.exitCode !== null) {
    return;
  }

  processHandle.kill();
  await waitForProcessExit(processHandle, 1500);

  if (processHandle.exitCode === null) {
    processHandle.kill("SIGKILL");
    await waitForProcessExit(processHandle, 1500);
  }
}

async function removeDirWithRetry(targetPath) {
  for (let attempt = 1; attempt <= 5; attempt += 1) {
    try {
      await fsp.rm(targetPath, { recursive: true, force: true });
      return;
    } catch (error) {
      if (!["EBUSY", "EPERM"].includes(error.code) || attempt === 5) {
        throw error;
      }
      await sleep(attempt * 300);
    }
  }
}

async function exists(targetPath) {
  try {
    await fsp.access(targetPath);
    return true;
  } catch {
    return false;
  }
}

async function findBrowser() {
  for (const candidate of CHROME_CANDIDATES) {
    if (await exists(candidate)) {
      return candidate;
    }
  }

  throw new Error("未找到可用的 Chrome/Edge 浏览器。");
}

async function waitForFile(filePath, timeoutMs) {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    if (await exists(filePath)) {
      return;
    }
    await sleep(100);
  }

  throw new Error(`等待文件超时: ${filePath}`);
}

async function getJson(url, timeoutMs = STARTUP_TIMEOUT_MS) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, { signal: controller.signal });
    if (!response.ok) {
      throw new Error(`HTTP ${response.status} ${response.statusText}`);
    }
    return await response.json();
  } finally {
    clearTimeout(timer);
  }
}

class CDPPage {
  constructor(webSocketUrl) {
    this.webSocketUrl = webSocketUrl;
    this.socket = null;
    this.nextId = 1;
    this.pending = new Map();
    this.eventListeners = new Map();
  }

  async connect() {
    await new Promise((resolve, reject) => {
      const socket = new WebSocket(this.webSocketUrl);
      this.socket = socket;

      socket.addEventListener("open", () => resolve());
      socket.addEventListener("error", (event) => {
        reject(event.error || new Error("CDP WebSocket 连接失败。"));
      });
      socket.addEventListener("message", (event) => this.onMessage(event.data));
      socket.addEventListener("close", () => {
        for (const { reject } of this.pending.values()) {
          reject(new Error("CDP WebSocket 已关闭。"));
        }
        this.pending.clear();
      });
    });
  }

  onMessage(rawMessage) {
    const message = JSON.parse(rawMessage.toString());

    if (typeof message.id === "number") {
      const pending = this.pending.get(message.id);
      if (!pending) {
        return;
      }

      this.pending.delete(message.id);

      if (message.error) {
        pending.reject(new Error(message.error.message || "CDP 调用失败。"));
      } else {
        pending.resolve(message.result || {});
      }
      return;
    }

    const listeners = this.eventListeners.get(message.method);
    if (!listeners || listeners.length === 0) {
      return;
    }

    for (const listener of [...listeners]) {
      try {
        if (!listener.predicate || listener.predicate(message.params || {})) {
          listener.resolve(message.params || {});
          if (!listener.persistent) {
            const current = this.eventListeners.get(message.method) || [];
            this.eventListeners.set(
              message.method,
              current.filter((entry) => entry !== listener),
            );
          }
        }
      } catch (error) {
        listener.reject(error);
      }
    }
  }

  send(method, params = {}) {
    const id = this.nextId++;
    const payload = JSON.stringify({ id, method, params });

    return new Promise((resolve, reject) => {
      this.pending.set(id, { resolve, reject });
      this.socket.send(payload);
    });
  }

  waitForEvent(method, { predicate, timeoutMs = NAVIGATION_TIMEOUT_MS } = {}) {
    return new Promise((resolve, reject) => {
      const entry = { resolve, reject, predicate, persistent: false };

      if (!this.eventListeners.has(method)) {
        this.eventListeners.set(method, []);
      }
      this.eventListeners.get(method).push(entry);

      const timer = setTimeout(() => {
        const current = this.eventListeners.get(method) || [];
        this.eventListeners.set(
          method,
          current.filter((listener) => listener !== entry),
        );
        reject(new Error(`等待事件超时: ${method}`));
      }, timeoutMs);

      const originalResolve = entry.resolve;
      const originalReject = entry.reject;

      entry.resolve = (value) => {
        clearTimeout(timer);
        originalResolve(value);
      };
      entry.reject = (error) => {
        clearTimeout(timer);
        originalReject(error);
      };
    });
  }

  async evaluate(expression, awaitPromise = true) {
    const result = await this.send("Runtime.evaluate", {
      expression,
      returnByValue: true,
      awaitPromise,
    });

    if (result.exceptionDetails) {
      throw new Error("页面脚本执行失败。");
    }

    return result.result ? result.result.value : undefined;
  }

  async close() {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.close();
    }
  }
}

async function launchBrowser(browserPath) {
  const tempDir = await fsp.mkdtemp(path.join(os.tmpdir(), "jst-shot-"));
  const profileDir = path.join(tempDir, "profile");
  await fsp.mkdir(profileDir, { recursive: true });

  const processHandle = spawn(
    browserPath,
    [
      "--headless=new",
      "--disable-gpu",
      "--hide-scrollbars",
      "--allow-file-access-from-files",
      "--disable-background-networking",
      "--disable-default-apps",
      "--disable-extensions",
      "--disable-sync",
      `--user-data-dir=${profileDir}`,
      "--remote-debugging-port=0",
      "about:blank",
    ],
    {
      stdio: ["ignore", "ignore", "pipe"],
      windowsHide: true,
    },
  );

  let stderr = "";
  processHandle.stderr.on("data", (chunk) => {
    stderr += chunk.toString();
  });

  const portFile = path.join(profileDir, "DevToolsActivePort");

  try {
    await waitForFile(portFile, STARTUP_TIMEOUT_MS);
    const [portLine] = (await fsp.readFile(portFile, "utf8")).split(/\r?\n/);
    const port = Number(portLine.trim());
    if (!Number.isFinite(port)) {
      throw new Error(`无效的调试端口: ${portLine}`);
    }

    const targetsUrl = `http://127.0.0.1:${port}/json/list`;
    const start = Date.now();
    let targets = [];

    while (Date.now() - start < STARTUP_TIMEOUT_MS) {
      targets = await getJson(targetsUrl, STARTUP_TIMEOUT_MS);
      const pageTarget = targets.find((target) => target.type === "page");
      if (pageTarget && pageTarget.webSocketDebuggerUrl) {
        return {
          browserProcess: processHandle,
          tempDir,
          webSocketUrl: pageTarget.webSocketDebuggerUrl,
        };
      }
      await sleep(100);
    }

    throw new Error("未找到可用的页面调试目标。");
  } catch (error) {
    await stopBrowserProcess(processHandle);
    await removeDirWithRetry(tempDir);
    const detail = stderr.trim() ? `\n浏览器输出:\n${stderr.trim()}` : "";
    throw new Error(`${error.message}${detail}`);
  }
}

async function preparePage(page) {
  await page.send("Page.enable");
  await page.send("Runtime.enable");
  await page.send("Network.enable");
}

function getViewportForFile(filePath) {
  const fileName = path.basename(filePath).toLowerCase();
  if (fileName === "design-system.html") {
    return DESKTOP_VIEWPORT;
  }
  return MOBILE_VIEWPORT;
}

async function setViewport(page, viewport) {
  await page.send("Emulation.setDeviceMetricsOverride", {
    width: viewport.width,
    height: viewport.height,
    deviceScaleFactor: viewport.deviceScaleFactor,
    mobile: viewport.mobile,
    screenWidth: viewport.width,
    screenHeight: viewport.height,
  });
  if (viewport.mobile) {
    await page.send("Emulation.setTouchEmulationEnabled", {
      enabled: true,
      maxTouchPoints: 5,
    });
  } else {
    await page.send("Emulation.setTouchEmulationEnabled", {
      enabled: false,
    });
  }
}

async function navigateAndSettle(page, url) {
  const loadEvent = page.waitForEvent("Page.loadEventFired");
  const navigateResult = await page.send("Page.navigate", { url });

  if (navigateResult.errorText) {
    throw new Error(`页面加载失败: ${navigateResult.errorText}`);
  }

  await loadEvent;
  await sleep(SETTLE_DELAY_MS);

  await page.evaluate(`
    (async () => {
      try {
        if (document.fonts && document.fonts.ready) {
          await document.fonts.ready;
        }
      } catch {}

      if (document.documentElement) {
        document.documentElement.style.scrollBehavior = "auto";
      }
      if (document.body) {
        document.body.style.scrollBehavior = "auto";
      }

      await new Promise((resolve) => {
        requestAnimationFrame(() => requestAnimationFrame(resolve));
      });

      return true;
    })()
  `);
}

async function scrollToBottom(page) {
  await page.evaluate(`
    (() => {
      const doc = document.documentElement;
      const body = document.body;
      const scroller = document.scrollingElement || doc || body;
      const viewportHeight = window.innerHeight;
      const maxScrollTop = Math.max(
        0,
        (scroller ? scroller.scrollHeight : 0) - viewportHeight,
        (doc ? doc.scrollHeight : 0) - viewportHeight,
        (body ? body.scrollHeight : 0) - viewportHeight
      );

      if (scroller) {
        scroller.scrollTop = maxScrollTop;
      }
      if (doc) {
        doc.scrollTop = maxScrollTop;
      }
      if (body) {
        body.scrollTop = maxScrollTop;
      }

      return {
        maxScrollTop,
        currentScrollTop: scroller ? scroller.scrollTop : 0,
        viewportHeight,
      };
    })()
  `);

  await sleep(POST_SCROLL_DELAY_MS);

  await page.evaluate(`
    (() => {
      const doc = document.documentElement;
      const body = document.body;
      const scroller = document.scrollingElement || doc || body;
      const viewportHeight = window.innerHeight;
      const maxScrollTop = Math.max(
        0,
        (scroller ? scroller.scrollHeight : 0) - viewportHeight,
        (doc ? doc.scrollHeight : 0) - viewportHeight,
        (body ? body.scrollHeight : 0) - viewportHeight
      );

      if (scroller) {
        scroller.scrollTop = maxScrollTop;
      }
      if (doc) {
        doc.scrollTop = maxScrollTop;
      }
      if (body) {
        body.scrollTop = maxScrollTop;
      }

      return scroller ? scroller.scrollTop : 0;
    })()
  `);
}

async function captureFullPage(page, outputPath) {
  const metrics = await page.send("Page.getLayoutMetrics");
  const cssContentSize = metrics.cssContentSize || metrics.contentSize;

  if (!cssContentSize) {
    throw new Error("无法获取页面内容尺寸。");
  }

  const screenshot = await page.send("Page.captureScreenshot", {
    format: "png",
    captureBeyondViewport: true,
    fromSurface: true,
    clip: {
      x: 0,
      y: 0,
      width: Math.max(1, Math.ceil(cssContentSize.width)),
      height: Math.max(1, Math.ceil(cssContentSize.height)),
      scale: 1,
    },
  });

  await fsp.writeFile(outputPath, Buffer.from(screenshot.data, "base64"));
}

async function listHtmlFiles(rootDir) {
  const entries = await fsp.readdir(rootDir, { withFileTypes: true });
  return entries
    .filter((entry) => entry.isFile() && entry.name.toLowerCase().endsWith(".html"))
    .map((entry) => path.join(rootDir, entry.name))
    .sort((left, right) =>
      path.basename(left).localeCompare(path.basename(right), "zh-CN"),
    );
}

async function main() {
  const browserPath = await findBrowser();
  const htmlFiles = await listHtmlFiles(ROOT_DIR);

  if (htmlFiles.length === 0) {
    throw new Error(`目录中未找到 HTML 文件: ${ROOT_DIR}`);
  }

  console.log(`截图目录: ${ROOT_DIR}`);
  console.log(`浏览器: ${browserPath}`);
  console.log(`页面数量: ${htmlFiles.length}`);

  const browser = await launchBrowser(browserPath);
  const page = new CDPPage(browser.webSocketUrl);

  try {
    await page.connect();
    await preparePage(page);

    const failures = [];

    for (let index = 0; index < htmlFiles.length; index += 1) {
      const htmlPath = htmlFiles[index];
      const fileName = path.basename(htmlPath);
      const outputPath = path.join(
        path.dirname(htmlPath),
        `${path.parse(fileName).name}.png`,
      );
      const viewport = getViewportForFile(htmlPath);

      process.stdout.write(`[${index + 1}/${htmlFiles.length}] ${fileName} ... `);

      try {
        await setViewport(page, viewport);
        await navigateAndSettle(page, pathToFileURL(htmlPath).href);
        await scrollToBottom(page);
        await captureFullPage(page, outputPath);
        console.log("完成");
      } catch (error) {
        console.log("失败");
        failures.push({ fileName, message: error.message });
      }
    }

    if (failures.length > 0) {
      console.error("\n以下文件截图失败：");
      for (const failure of failures) {
        console.error(`- ${failure.fileName}: ${failure.message}`);
      }
      process.exitCode = 1;
    } else {
      console.log("\n全部截图完成。");
    }
  } finally {
    await page.close();
    await stopBrowserProcess(browser.browserProcess);
    await removeDirWithRetry(browser.tempDir);
  }
}

main().catch((error) => {
  console.error(error.message);
  process.exit(1);
});
