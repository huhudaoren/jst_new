import { z } from 'zod';
import { readdir } from 'fs/promises';
import config from '../config.js';

export const name = 'mp_run_flow_list';

export const description =
  'List all available YAML test flows in the flows directory. Files prefixed with "_" are excluded.';

export const inputSchema = z.object({});

export async function handler() {
  let files;
  try {
    files = await readdir(config.flowsDir);
  } catch (err) {
    if (err.code === 'ENOENT') {
      return {
        content: [{ type: 'text', text: `Flows directory not found: ${config.flowsDir}` }],
      };
    }
    throw err;
  }

  const flows = files
    .filter((f) => f.endsWith('.yaml') && !f.startsWith('_'))
    .map((f) => f.replace(/\.yaml$/, ''));

  if (flows.length === 0) {
    return {
      content: [{ type: 'text', text: 'No flow files found in flows directory.' }],
    };
  }

  return {
    content: [
      {
        type: 'text',
        text: `Available flows (${flows.length}):\n${flows.map((f) => `  - ${f}`).join('\n')}`,
      },
    ],
  };
}
