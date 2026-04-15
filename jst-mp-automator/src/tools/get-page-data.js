import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_get_page_data';

export const description =
  'Get the data object of the current page. Optionally provide a dot-path to drill into a specific key.';

export const inputSchema = z.object({
  path: z.string().optional().describe('Dot-path to a specific data key, e.g. "list.0.name"'),
});

export async function handler(args) {
  const data = await bot.getPageData(args.path);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
