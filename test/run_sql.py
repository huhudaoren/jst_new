import pymysql, sys, re

path = sys.argv[1]
with open(path, encoding='utf-8') as f:
    sql = f.read()

# Strip -- line comments and /* */ block comments
sql = re.sub(r'/\*.*?\*/', '', sql, flags=re.DOTALL)
sql = '\n'.join(l for l in sql.splitlines() if not l.strip().startswith('--'))

conn = pymysql.connect(host='59.110.53.165', port=3306, user='jst_new',
                       password='J8zZpAa4zG8y6a7e', database='jst_new',
                       charset='utf8mb4', autocommit=True)
cur = conn.cursor()

# naive split by `;` at line end — but JSON_OBJECT etc have no ; inside. Good enough.
stmts = [s.strip() for s in sql.split(';') if s.strip()]
print(f"Total statements: {len(stmts)}")
ok = 0
errs = []
for i, s in enumerate(stmts, 1):
    try:
        cur.execute(s)
        ok += 1
    except Exception as e:
        errs.append((i, str(e)[:200], s[:150].replace('\n',' ')))
print(f"OK: {ok}, Errors: {len(errs)}")
for e in errs[:20]:
    print(f"  #{e[0]}: {e[1]}\n    SQL: {e[2]}")
conn.close()
