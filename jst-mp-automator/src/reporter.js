import { writeFile, mkdir } from 'fs/promises';
import { join } from 'path';
import config from './config.js';

/**
 * Generate a human-readable text report from a flow report object.
 * @param {object} report - Structured report from runFlow()
 * @returns {string} Text report
 */
export function generateTextReport(report) {
  const lines = [];

  // Header
  lines.push(`=== Flow Report: ${report.flow} ===`);
  lines.push(`Description: ${report.description || '(none)'}`);
  lines.push(`Login: ${report.login || '(anonymous)'}`);
  lines.push(`Timestamp: ${report.timestamp}`);
  lines.push(`Duration: ${report.duration_ms}ms`);
  lines.push('');

  // Preconditions
  if (report.preconditions && report.preconditions.length > 0) {
    lines.push('--- Preconditions ---');
    for (const pre of report.preconditions) {
      const icon = pre.result === 'pass' ? '[PASS]' : pre.result === 'fixed' ? '[FIXED]' : '[FAIL]';
      lines.push(`  ${icon} ${pre.expect} | SQL: ${pre.sql.substring(0, 80)}`);
    }
    lines.push('');
  }

  // Steps
  lines.push('--- Steps ---');
  for (const step of report.steps) {
    const icon = step.status === 'pass' ? '[PASS]' : step.status === 'fail' ? '[FAIL]' : '[SKIP]';
    lines.push(`  ${icon} #${step.index} ${step.name}`);

    if (step.error) {
      lines.push(`       Error: ${step.error}`);
    }

    for (const assertion of step.assertions) {
      const aIcon = assertion.result === 'pass' ? '[PASS]' : '[FAIL]';
      switch (assertion.type) {
        case 'screenshot':
          lines.push(`       ${aIcon} screenshot captured`);
          break;
        case 'element':
          lines.push(`       ${aIcon} element "${assertion.selector}" expect=${assertion.expect} actual=${assertion.actual}`);
          break;
        case 'page_data':
          lines.push(`       ${aIcon} page_data "${assertion.path}" expect=${assertion.expect} actual=${assertion.actual}`);
          break;
        case 'db':
          lines.push(`       ${aIcon} db expect=${assertion.expect} actual=${JSON.stringify(assertion.actual)}`);
          break;
        case 'product_review':
          lines.push(`       ${aIcon} product_review: ${assertion.questions.length} question(s)`);
          break;
      }
    }

    if (step.productReview.length > 0) {
      lines.push('       Product review questions:');
      for (const q of step.productReview) {
        lines.push(`         - ${q}`);
      }
    }
  }
  lines.push('');

  // Summary
  lines.push('--- Summary ---');
  lines.push(`  Total: ${report.summary.total} | Passed: ${report.summary.passed} | Failed: ${report.summary.failed} | Skipped: ${report.summary.skipped}`);

  const allPassed = report.summary.failed === 0;
  lines.push(`  Result: ${allPassed ? 'ALL PASSED' : 'HAS FAILURES'}`);

  if (report.summary.productIssues.length > 0) {
    lines.push('');
    lines.push('--- Product Review ---');
    for (const issue of report.summary.productIssues) {
      lines.push(`  Step #${issue.step} "${issue.name}":`);
      for (const q of issue.questions) {
        lines.push(`    - ${q}`);
      }
    }
  }

  lines.push('');
  lines.push('=== End Report ===');

  return lines.join('\n');
}

/**
 * Save report JSON to config.reportsDir/{flowName}-{timestamp}.json
 * @param {object} report - Report object
 * @param {string} flowName - Flow name for the filename
 * @returns {Promise<string>} Path to saved report file
 */
export async function saveReport(report, flowName) {
  await mkdir(config.reportsDir, { recursive: true });

  const ts = report.timestamp.replace(/[:.]/g, '-');
  const filename = `${flowName}-${ts}.json`;
  const filePath = join(config.reportsDir, filename);

  await writeFile(filePath, JSON.stringify(report, null, 2), 'utf-8');
  console.error(`[reporter] report saved to ${filePath}`);
  return filePath;
}
