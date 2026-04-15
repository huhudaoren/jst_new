import { z } from 'zod';
import * as bot from '../automator.js';

export const name = 'mp_screenshot';

export const description =
  'Take a screenshot of the current page. Use full_page=true to capture the entire scrollable content as multiple segments.';

export const inputSchema = z.object({
  full_page: z.boolean().optional().describe('If true, capture the full scrollable page as multiple screenshots'),
  label: z.string().optional().describe('Optional label for the screenshot'),
});

export async function handler(args) {
  const label = args.label || 'screenshot';

  if (!args.full_page) {
    // Single viewport screenshot
    const base64 = await bot.screenshot();
    return {
      content: [
        { type: 'text', text: `[${label}] viewport screenshot` },
        { type: 'image', data: base64, mimeType: 'image/png' },
      ],
    };
  }

  // Full-page: get scroll height, capture in segments
  const viewportHeight = await bot.evaluate(() => {
    // eslint-disable-next-line no-undef
    const info = wx.getSystemInfoSync();
    return info.windowHeight;
  });

  const scrollHeight = await bot.evaluate(() => {
    return new Promise((resolve) => {
      // eslint-disable-next-line no-undef
      wx.createSelectorQuery()
        .selectViewport()
        .scrollOffset(function (res) {
          resolve(res ? res.scrollHeight || 800 : 800);
        })
        .exec();
    });
  });

  const totalHeight = Math.max(scrollHeight, viewportHeight);
  const segments = Math.ceil(totalHeight / viewportHeight);
  const content = [];

  for (let i = 0; i < segments; i++) {
    const scrollTop = i * viewportHeight;

    // Scroll to position
    await bot.evaluate((top) => {
      // eslint-disable-next-line no-undef
      wx.pageScrollTo({ scrollTop: top, duration: 0 });
    }, scrollTop);

    await bot.sleep(500);

    const base64 = await bot.screenshot();
    content.push({
      type: 'text',
      text: `[${i + 1}/${segments}] scroll position ${scrollTop}px`,
    });
    content.push({
      type: 'image',
      data: base64,
      mimeType: 'image/png',
    });
  }

  return { content };
}
