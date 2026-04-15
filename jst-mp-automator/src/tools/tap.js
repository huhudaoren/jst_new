import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_tap';

export const description = 'Tap (click) an element on the current page by CSS selector.';

export const inputSchema = z.object({
  selector: z.string().describe('CSS selector for the element to tap'),
});

export async function handler(args) {
  await bot.tapElement(args.selector);
  return {
    content: [{ type: 'text', text: `Tapped: ${args.selector}` }],
  };
}
