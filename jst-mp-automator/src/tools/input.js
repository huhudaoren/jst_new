import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_input';

export const description = 'Type text into an input element on the current page.';

export const inputSchema = z.object({
  selector: z.string().describe('CSS selector for the input element'),
  value: z.string().describe('Text value to input'),
});

export async function handler(args) {
  await bot.inputText(args.selector, args.value);
  return {
    content: [{ type: 'text', text: `Input "${args.value}" into: ${args.selector}` }],
  };
}
