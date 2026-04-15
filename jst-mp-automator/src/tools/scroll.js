import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_scroll';

export const description = 'Scroll the current page up or down by a given distance in pixels.';

export const inputSchema = z.object({
  direction: z.enum(['up', 'down']).describe('Scroll direction'),
  distance: z.number().optional().describe('Scroll distance in pixels (default 300)'),
});

export async function handler(args) {
  const distance = args.distance ?? 300;
  await bot.scrollPage(args.direction, distance);
  return {
    content: [{ type: 'text', text: `Scrolled ${args.direction} by ${distance}px` }],
  };
}
