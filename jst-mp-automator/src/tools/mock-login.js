import { z } from 'zod';
import config from '../config.js';
import * as bot from '../automator.js';

export const name = 'mp_mock_login';

export const description =
  'Mock login to the mini program by calling the backend login API with a mock code, then injecting the token into storage and navigating to the "My" page.';

export const inputSchema = z.object({
  mockCode: z.string().describe('Mock auth code to send to the backend login endpoint'),
});

export async function handler(args) {
  const url = `${config.backendUrl}/jst/wx/auth/login`;

  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code: args.mockCode }),
  });

  if (!resp.ok) {
    throw new Error(`Login request failed: HTTP ${resp.status} ${resp.statusText}`);
  }

  const body = await resp.json();

  if (body.code !== 200 && body.code !== 0) {
    throw new Error(`Login API error: ${body.msg || JSON.stringify(body)}`);
  }

  const token = body.data?.token || body.token;
  if (!token) {
    throw new Error(`No token in login response: ${JSON.stringify(body)}`);
  }

  // Inject token into mini program storage
  const mp = bot.getMiniProgram();
  const page = await mp.currentPage();
  await page.evaluate((t) => {
    // eslint-disable-next-line no-undef
    wx.setStorageSync('jst-token', t);
  }, token);

  // Navigate to "My" page to confirm login
  await bot.navigateTo('/pages/my/index');

  const summary = body.data || body;
  return {
    content: [
      {
        type: 'text',
        text: [
          `Login successful.`,
          `Token: ${token.substring(0, 20)}...`,
          `User info: ${JSON.stringify(summary, null, 2)}`,
        ].join('\n'),
      },
    ],
  };
}
