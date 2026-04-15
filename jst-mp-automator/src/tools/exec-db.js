import { z } from 'zod';
import * as db from '../db.js';

export const name = 'mp_exec_db';

export const description =
  'Execute an INSERT, UPDATE, or DELETE SQL statement against the test database. DROP/TRUNCATE/ALTER are blocked for safety.';

export const inputSchema = z.object({
  sql: z.string().describe('SQL statement (INSERT, UPDATE, or DELETE)'),
});

export async function handler(args) {
  const result = await db.exec(args.sql);
  return {
    content: [
      {
        type: 'text',
        text: `Executed. Affected rows: ${result.affectedRows}, Insert ID: ${result.insertId}`,
      },
    ],
  };
}
