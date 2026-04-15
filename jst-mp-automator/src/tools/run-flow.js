import { z } from 'zod';
import { loadFlow, runFlow } from '../flow-engine.js';
import { generateTextReport, saveReport } from '../reporter.js';

export const name = 'mp_run_flow';

export const description =
  'Run a pre-defined YAML test flow by name. Executes login, preconditions, steps with assertions, and returns a structured report with screenshots for failed steps and product review questions.';

export const inputSchema = z.object({
  flowName: z.string().describe('Name of the YAML flow file (without .yaml extension)'),
  params: z.record(z.string()).optional().describe('Parameters to pass to the flow'),
});

export async function handler(args) {
  // Load the YAML flow
  const flow = await loadFlow(args.flowName);

  // Execute the flow
  const report = await runFlow(flow, args.params || {});

  // Save report to disk
  const reportPath = await saveReport(report, args.flowName);

  // Generate text summary
  const textReport = generateTextReport(report);

  // Build MCP response content
  const content = [];

  // 1. Text report summary
  content.push({
    type: 'text',
    text: textReport + `\n\nReport saved to: ${reportPath}`,
  });

  // 2. For failed steps or steps with product_review: include screenshots + questions
  for (const step of report.steps) {
    const hasFail = step.status === 'fail';
    const hasReview = step.productReview.length > 0;

    if (!hasFail && !hasReview) continue;

    // Include screenshots as images
    for (const base64 of step.screenshots) {
      content.push({
        type: 'image',
        data: base64,
        mimeType: 'image/png',
      });
    }

    // For product review, add the questions as text for Claude to analyze
    if (hasReview) {
      content.push({
        type: 'text',
        text: [
          `--- Product Review for Step #${step.index} "${step.name}" ---`,
          'Please analyze the screenshot above and answer these questions:',
          ...step.productReview.map((q, i) => `  ${i + 1}. ${q}`),
        ].join('\n'),
      });
    }

    // For failures, add context
    if (hasFail && !hasReview) {
      const failedAssertions = step.assertions
        .filter((a) => a.result === 'fail')
        .map((a) => {
          if (a.type === 'element') return `element "${a.selector}" expected ${a.expect}, got ${a.actual}`;
          if (a.type === 'page_data') return `page_data "${a.path}" expected ${a.expect}, got ${a.actual}`;
          if (a.type === 'db') return `db expected ${a.expect}, got ${JSON.stringify(a.actual)}`;
          return `${a.type} assertion failed`;
        });

      content.push({
        type: 'text',
        text: [
          `--- Failure Detail for Step #${step.index} "${step.name}" ---`,
          step.error ? `Error: ${step.error}` : '',
          failedAssertions.length > 0 ? `Failed assertions:\n${failedAssertions.map((a) => `  - ${a}`).join('\n')}` : '',
          'Please analyze the screenshot above to help diagnose the issue.',
        ].filter(Boolean).join('\n'),
      });
    }
  }

  return { content };
}
