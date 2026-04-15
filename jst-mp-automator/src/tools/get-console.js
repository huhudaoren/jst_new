import { z } from 'zod';

export const name = 'mp_get_console';

export const description =
  'Get console log output from the mini program. Note: miniprogram-automator does not expose a console capture API, so this returns a hint about where to check logs.';

export const inputSchema = z.object({
  level: z
    .enum(['all', 'error', 'warn', 'log'])
    .optional()
    .describe('Filter by log level (default: all)'),
});

export async function handler(args) {
  const level = args.level || 'all';
  return {
    content: [
      {
        type: 'text',
        text: [
          `Console capture is not supported by miniprogram-automator SDK.`,
          `To view ${level === 'all' ? 'all' : level} logs:`,
          `  1. Open WeChat DevTools manually`,
          `  2. Go to the "Console" panel`,
          `  3. Filter by level: ${level}`,
          ``,
          `Tip: Use mp_get_page_data to inspect page state, or mp_evaluate to run diagnostic code.`,
        ].join('\n'),
      },
    ],
  };
}
