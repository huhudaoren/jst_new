import { z } from 'zod';
import * as db from '../db.js';

export const name = 'mp_query_db';

export const description =
  'Execute a read-only SQL SELECT query against the test database and return rows as JSON.';

export const inputSchema = z.object({
  sql: z.string().describe('SQL SELECT statement to execute'),
});

export async function handler(args) {
  const rows = await db.query(args.sql);
  return {
    content: [
      {
        type: 'text',
        text: `${rows.length} row(s) returned:\n${JSON.stringify(rows, null, 2)}`,
      },
    ],
  };
}
