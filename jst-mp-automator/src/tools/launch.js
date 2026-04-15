import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_launch';

export const description =
  'Launch or connect to WeChat DevTools and return the current page path.';

export const inputSchema = z.object({
  projectPath: z.string().optional().describe('Override the default project path'),
});

export async function handler(args) {
  await bot.launch();
  const page = await bot.getCurrentPage();
  const path = await page.path();
  return {
    content: [{ type: 'text', text: `DevTools connected. Current page: ${path}` }],
  };
}
