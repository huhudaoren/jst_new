import automator from 'miniprogram-automator';
import config from './config.js';

let miniProgram = null;

export async function sleep(ms) {
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

const TAB_PAGES = [
  '/pages/index/index',
  '/pages/contest/list',
  '/pages/course/list',
  '/pages/notice/list',
  '/pages/my/index'
];

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
  const isTab = TAB_PAGES.includes(path);

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

export { withRetry };
