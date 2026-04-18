import { readFile } from 'fs/promises';
import { join } from 'path';
import yaml from 'js-yaml';
import config from './config.js';
import * as bot from './automator.js';
import * as db from './db.js';

/**
 * Load and parse a YAML flow file from config.flowsDir.
 * @param {string} flowName - Name of the flow (without .yaml extension)
 * @returns {Promise<object>} Parsed flow object
 */
export async function loadFlow(flowName) {
  const filePath = join(config.flowsDir, `${flowName}.yaml`);
  const content = await readFile(filePath, 'utf-8');
  const flow = yaml.load(content);

  if (!flow || !flow.name || !Array.isArray(flow.steps)) {
    throw new Error(`Invalid flow file: ${filePath} — must have "name" and "steps" fields`);
  }

  return flow;
}

/**
 * Evaluate a simple expect expression against a DB row.
 * Supports: "field >= 1", "status = 'paid'", "cnt != 0", etc.
 * Operators: >=, <=, >, <, =, !=
 * @param {object} row - A single database row
 * @param {string} expectStr - Expression string
 * @returns {boolean}
 */
function evaluateExpect(row, expectStr) {
  const match = expectStr.match(/^(\w+)\s*(>=|<=|!=|>|<|=)\s*(.+)$/);
  if (!match) {
    throw new Error(`Cannot parse expect expression: "${expectStr}"`);
  }

  const [, field, operator, rawValue] = match;
  const actual = row[field];

  // Parse the expected value — strip quotes for strings, parse numbers otherwise
  let expected;
  const trimmed = rawValue.trim();
  if ((trimmed.startsWith("'") && trimmed.endsWith("'")) ||
      (trimmed.startsWith('"') && trimmed.endsWith('"'))) {
    expected = trimmed.slice(1, -1);
  } else {
    expected = Number(trimmed);
  }

  switch (operator) {
    case '>=': return actual >= expected;
    case '<=': return actual <= expected;
    case '>':  return actual > expected;
    case '<':  return actual < expected;
    case '=':  return actual == expected; // eslint-disable-line eqeqeq
    case '!=': return actual != expected; // eslint-disable-line eqeqeq
    default:   return false;
  }
}

/**
 * Perform mock login: POST to backend, inject token, reLaunch to /pages/my/index.
 * @param {string} mockCode - Mock auth code (e.g. "MOCK_1001")
 */
async function performLogin(mockCode) {
  const url = `${config.backendUrl}/jst/wx/auth/login`;
  console.error(`[flow-engine] logging in with code: ${mockCode}`);

  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code: mockCode }),
  });

  if (!resp.ok) {
    throw new Error(`Login HTTP ${resp.status} ${resp.statusText}`);
  }

  const body = await resp.json();
  if (body.code !== 200 && body.code !== 0) {
    throw new Error(`Login API error: ${body.msg || JSON.stringify(body)}`);
  }

  const token = body.data?.token || body.token;
  if (!token) {
    throw new Error(`No token in response: ${JSON.stringify(body)}`);
  }

  // Inject token into mini program storage
  const mp = bot.getMiniProgram();
  await mp.evaluate((t) => {
    // eslint-disable-next-line no-undef
    wx.setStorageSync('jst-token', t);
  }, token);

  // Navigate to My page to confirm login state
  await bot.reLaunchTo('/pages/my/index');
  console.error(`[flow-engine] login successful, token: ${token.substring(0, 20)}...`);
}

/**
 * Run precondition checks. If a check fails and fix_sql is provided, run the fix.
 * @param {Array} preconditions
 * @returns {Promise<Array>} Results of precondition checks
 */
async function runPreconditions(preconditions) {
  if (!preconditions || preconditions.length === 0) return [];

  const results = [];
  for (const pre of preconditions) {
    const rows = await db.query(pre.sql);
    const row = rows[0] || {};
    const passed = evaluateExpect(row, pre.expect);

    if (!passed && pre.fix_sql) {
      console.error(`[flow-engine] precondition failed, running fix_sql: ${pre.fix_sql.substring(0, 80)}...`);
      await db.exec(pre.fix_sql);
      results.push({ sql: pre.sql, expect: pre.expect, result: 'fixed' });
    } else if (!passed) {
      results.push({ sql: pre.sql, expect: pre.expect, result: 'fail', actual: row });
    } else {
      results.push({ sql: pre.sql, expect: pre.expect, result: 'pass' });
    }
  }
  return results;
}

/**
 * Execute a single step action.
 * @param {object} step - Flow step definition
 */
async function executeAction(step) {
  switch (step.action) {
    case 'navigate':
      await bot.navigateTo(step.path, step.query || {});
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
      await bot.scrollPage(step.direction || 'down', step.distance || 300);
      break;
    case 'wait':
      await bot.sleep(step.wait || 1000);
      break;
    case 'login':
      await performLogin(step.value || step.code);
      break;
    default:
      throw new Error(`Unknown action: ${step.action}`);
  }
}

/**
 * Run assertion checks for a step.
 * @param {Array} checks - Array of check definitions
 * @returns {Promise<{assertions: Array, screenshots: Array, productReview: Array}>}
 */
async function runChecks(checks) {
  const assertions = [];
  const screenshots = [];
  const productReview = [];

  if (!checks || checks.length === 0) return { assertions, screenshots, productReview };

  for (const check of checks) {
    // Screenshot check
    if (check.screenshot != null) {
      const base64 = await bot.screenshot();
      screenshots.push(base64);
      assertions.push({ type: 'screenshot', result: 'pass' });
      continue;
    }

    // Element existence check
    if (check.element != null) {
      const expect = check.expect || 'exists';
      try {
        const el = await bot.getElement(check.element);
        const found = !!el;
        const passed = expect === 'exists' ? found : !found;
        assertions.push({
          type: 'element',
          selector: check.element,
          expect,
          result: passed ? 'pass' : 'fail',
          actual: found ? 'exists' : 'not_exists',
        });
      } catch {
        // Element not found throws
        const passed = expect === 'not_exists';
        assertions.push({
          type: 'element',
          selector: check.element,
          expect,
          result: passed ? 'pass' : 'fail',
          actual: 'not_exists',
        });
      }
      continue;
    }

    // Page data check
    if (check.page_data != null) {
      const value = await bot.getPageData(check.page_data);
      const expect = check.expect || 'not_empty';
      let passed;
      if (expect === 'not_empty') {
        passed = value != null && value !== '' && (typeof value !== 'object' || (Array.isArray(value) ? value.length > 0 : Object.keys(value).length > 0));
      } else {
        passed = String(value) === String(expect);
      }
      assertions.push({
        type: 'page_data',
        path: check.page_data,
        expect,
        result: passed ? 'pass' : 'fail',
        actual: typeof value === 'object' ? JSON.stringify(value) : String(value),
      });
      continue;
    }

    // Database check
    if (check.db != null) {
      const rows = await db.query(check.db);
      const row = rows[0] || {};
      const passed = evaluateExpect(row, check.expect);
      assertions.push({
        type: 'db',
        sql: check.db,
        expect: check.expect,
        result: passed ? 'pass' : 'fail',
        actual: row,
      });
      continue;
    }

    // Product review — just collect questions for Claude to analyze
    if (check.product_review != null) {
      const questions = Array.isArray(check.product_review)
        ? check.product_review
        : [check.product_review];
      productReview.push(...questions);

      // Take a screenshot for product review context
      const base64 = await bot.screenshot();
      screenshots.push(base64);
      assertions.push({ type: 'product_review', result: 'pass', questions });
      continue;
    }
  }

  return { assertions, screenshots, productReview };
}

/**
 * Execute a complete flow and return a structured report.
 * @param {object} flow - Parsed flow object
 * @param {object} params - Optional parameters (for future template substitution)
 * @returns {Promise<object>} Report object
 */
export async function runFlow(flow, params = {}) {
  const startTime = Date.now();
  const report = {
    flow: flow.name,
    description: flow.description || '',
    login: flow.login || null,
    timestamp: new Date().toISOString(),
    duration_ms: 0,
    preconditions: [],
    steps: [],
    summary: { total: 0, passed: 0, failed: 0, skipped: 0, productIssues: [] },
  };

  try {
    // --- Login ---
    if (flow.login) {
      await performLogin(flow.login);
    }

    // --- Preconditions ---
    report.preconditions = await runPreconditions(flow.precondition);
    const preconditionFailed = report.preconditions.some((p) => p.result === 'fail');
    if (preconditionFailed) {
      console.error('[flow-engine] precondition failed without fix_sql, continuing anyway');
    }

    // --- Steps ---
    let aborted = false;
    for (let i = 0; i < flow.steps.length; i++) {
      const stepDef = flow.steps[i];
      const stepResult = {
        index: i,
        name: stepDef.name || `Step ${i}`,
        status: 'pass',
        screenshots: [],
        assertions: [],
        productReview: [],
        error: null,
      };

      if (aborted) {
        stepResult.status = 'skipped';
        report.steps.push(stepResult);
        report.summary.skipped++;
        continue;
      }

      try {
        // Execute the action
        await executeAction(stepDef);

        // Wait after action if specified
        if (stepDef.wait) {
          await bot.sleep(stepDef.wait);
        }

        // Run checks
        const { assertions, screenshots, productReview } = await runChecks(stepDef.check);
        stepResult.assertions = assertions;
        stepResult.screenshots = screenshots;
        stepResult.productReview = productReview;

        // Determine step status
        const hasFailure = assertions.some((a) => a.result === 'fail');
        if (hasFailure) {
          stepResult.status = 'fail';
        }
      } catch (err) {
        stepResult.status = 'fail';
        stepResult.error = err.message || String(err);
        console.error(`[flow-engine] step ${i} "${stepDef.name}" error: ${stepResult.error}`);

        // Try to capture a screenshot on failure
        try {
          const base64 = await bot.screenshot();
          stepResult.screenshots.push(base64);
        } catch {
          // Ignore screenshot failure
        }
      }

      report.steps.push(stepResult);

      // Update summary
      report.summary.total++;
      if (stepResult.status === 'pass') {
        report.summary.passed++;
      } else if (stepResult.status === 'fail') {
        report.summary.failed++;
        if (stepDef.on_fail === 'abort') {
          aborted = true;
          console.error(`[flow-engine] step ${i} failed with on_fail=abort, skipping remaining steps`);
        }
      }

      // Collect product review issues
      if (stepResult.productReview.length > 0) {
        report.summary.productIssues.push({
          step: i,
          name: stepDef.name,
          questions: stepResult.productReview,
        });
      }
    }
  } catch (err) {
    // Top-level error (login or precondition failure)
    report.steps.push({
      index: -1,
      name: 'flow-setup',
      status: 'fail',
      screenshots: [],
      assertions: [],
      productReview: [],
      error: err.message || String(err),
    });
    report.summary.total++;
    report.summary.failed++;
  }

  report.duration_ms = Date.now() - startTime;
  return report;
}
