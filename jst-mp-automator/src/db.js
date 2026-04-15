import mysql from 'mysql2/promise';
import config from './config.js';

let pool = null;

/**
 * Get or create the MySQL connection pool (lazy init).
 */
function getPool() {
  if (!pool) {
    console.error('[db] creating connection pool');
    pool = mysql.createPool(config.db);
  }
  return pool;
}

/**
 * Execute a SELECT query. Only SELECT statements are allowed.
 * @param {string} sql - SQL query (must start with SELECT)
 * @returns {Promise<Array>} - Result rows
 */
export async function query(sql) {
  const trimmed = sql.trim().toUpperCase();
  if (!trimmed.startsWith('SELECT')) {
    throw new Error('[db] query() only allows SELECT statements. Use exec() for INSERT/UPDATE/DELETE.');
  }

  const p = getPool();
  console.error(`[db] query: ${sql.trim().substring(0, 120)}...`);
  const [rows] = await p.execute(sql);
  console.error(`[db] returned ${rows.length} rows`);
  return rows;
}

/**
 * Execute an INSERT, UPDATE, or DELETE statement.
 * Blocks DROP, TRUNCATE, and ALTER for safety.
 * @param {string} sql - SQL statement
 * @returns {Promise<{affectedRows: number, insertId: number}>}
 */
export async function exec(sql) {
  const trimmed = sql.trim().toUpperCase();

  // Block dangerous operations
  const blocked = ['DROP', 'TRUNCATE', 'ALTER', 'CREATE', 'GRANT', 'REVOKE'];
  for (const keyword of blocked) {
    if (trimmed.startsWith(keyword)) {
      throw new Error(`[db] exec() blocks ${keyword} statements for safety.`);
    }
  }

  // Only allow INSERT, UPDATE, DELETE
  const allowed = ['INSERT', 'UPDATE', 'DELETE'];
  const startsWithAllowed = allowed.some(kw => trimmed.startsWith(kw));
  if (!startsWithAllowed) {
    throw new Error(`[db] exec() only allows INSERT/UPDATE/DELETE. Got: ${trimmed.substring(0, 20)}...`);
  }

  const p = getPool();
  console.error(`[db] exec: ${sql.trim().substring(0, 120)}...`);
  const [result] = await p.execute(sql);
  console.error(`[db] affectedRows=${result.affectedRows}, insertId=${result.insertId}`);
  return {
    affectedRows: result.affectedRows,
    insertId: result.insertId,
  };
}

/**
 * Close the connection pool.
 */
export async function closePool() {
  if (pool) {
    console.error('[db] closing connection pool');
    await pool.end();
    pool = null;
  }
}
