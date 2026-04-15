import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_navigate';

export const description =
  'Navigate to a mini-program page by path. Use reLaunch to clear the page stack.';

export const inputSchema = z.object({
  path: z.string().describe('Page path, e.g. /pages/index/index'),
  query: z.record(z.string()).optional().describe('Query parameters as key-value pairs'),
  reLaunch: z.boolean().optional().describe('If true, use reLaunch instead of navigateTo'),
});

export async function handler(args) {
  if (args.reLaunch) {
    await bot.reLaunchTo(args.path);
  } else {
    await bot.navigateTo(args.path, args.query || {});
  }
  const page = await bot.getCurrentPage();
  const currentPath = await page.path();
  return {
    content: [{ type: 'text', text: `Navigated. Current page: ${currentPath}` }],
  };
}
