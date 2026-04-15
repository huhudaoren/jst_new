import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_get_element';

export const description =
  'Get element info (text, wxml, offset, size) by CSS selector. Returns "not found" if the element does not exist.';

export const inputSchema = z.object({
  selector: z.string().describe('CSS selector for the element'),
});

export async function handler(args) {
  try {
    const info = await bot.getElement(args.selector);
    return {
      content: [{ type: 'text', text: JSON.stringify(info, null, 2) }],
    };
  } catch (err) {
    if (err.message && err.message.includes('not found')) {
      return {
        content: [{ type: 'text', text: `Element not found: ${args.selector}` }],
      };
    }
    throw err;
  }
}
