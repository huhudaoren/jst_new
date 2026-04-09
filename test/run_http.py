#!/usr/bin/env python3
"""Minimal .http runner for wx-tests.http / admin-tests.http.
Supports: @var, {{var}}, ### block titles, JSON bodies,
`> {% client.global.set("k", response.body.X.Y) %}` extraction.
Filter by regex on block title.
"""
import re
import sys
import json
import requests

def parse_http(path):
    with open(path, encoding='utf-8') as f:
        text = f.read()
    # Collect file-level @vars
    vars_ = {}
    lines = text.splitlines()
    i = 0
    while i < len(lines):
        m = re.match(r'^@(\w+)\s*=\s*(.*)$', lines[i])
        if m:
            vars_[m.group(1)] = m.group(2).strip()
        i += 1
    # Split by ###
    blocks = []
    cur = None
    for line in lines:
        if line.startswith('###'):
            if cur:
                blocks.append(cur)
            cur = {'title': line.lstrip('# ').strip(), 'lines': []}
        else:
            if cur is not None:
                cur['lines'].append(line)
    if cur:
        blocks.append(cur)
    return vars_, blocks

def substitute(s, vars_):
    def repl(m):
        k = m.group(1)
        return str(vars_.get(k, m.group(0)))
    return re.sub(r'\{\{(\w+)\}\}', repl, s)

def parse_block(block, vars_):
    """Return (method, url, headers, body, post_script) or None if not a request."""
    lines = block['lines']
    # Find first non-empty line that looks like METHOD URL
    i = 0
    while i < len(lines) and not lines[i].strip():
        i += 1
    if i >= len(lines):
        return None
    m = re.match(r'^(GET|POST|PUT|DELETE|PATCH)\s+(.+)$', lines[i].strip())
    if not m:
        return None
    method = m.group(1)
    url = substitute(m.group(2).strip(), vars_)
    i += 1
    headers = {}
    while i < len(lines):
        line = lines[i]
        if not line.strip():
            i += 1
            break
        mh = re.match(r'^([\w-]+):\s*(.*)$', line)
        if mh:
            headers[mh.group(1)] = substitute(mh.group(2).strip(), vars_)
        i += 1
    # Body until `> {%` or end
    body_lines = []
    post = None
    while i < len(lines):
        line = lines[i]
        if line.startswith('> {%'):
            # Capture until `%}`
            post_lines = []
            i += 1
            while i < len(lines) and '%}' not in lines[i]:
                post_lines.append(lines[i])
                i += 1
            post = '\n'.join(post_lines)
            break
        body_lines.append(line)
        i += 1
    body = substitute('\n'.join(body_lines).strip(), vars_)
    return method, url, headers, body, post

def apply_post(post, resp_json, vars_):
    if not post:
        return
    # Handle client.global.set("k", response.body.a.b);
    for m in re.finditer(r'client\.global\.set\("(\w+)",\s*response\.body((?:\.\w+)+)\)', post):
        key = m.group(1)
        path = m.group(2).lstrip('.').split('.')
        val = resp_json
        try:
            for p in path:
                val = val[p] if val is not None else None
            if val is not None:
                vars_[key] = str(val)
        except (KeyError, TypeError):
            pass

def run(path, filter_re):
    vars_, blocks = parse_http(path)
    pat = re.compile(filter_re)
    results = []
    for block in blocks:
        title = block['title']
        if not pat.search(title):
            continue
        parsed = parse_block(block, vars_)
        if not parsed:
            continue
        method, url, headers, body, post = parsed
        try:
            kw = {'headers': headers, 'timeout': 60}
            if body:
                kw['data'] = body.encode('utf-8')
            r = requests.request(method, url, **kw)
            status = r.status_code
            try:
                rj = r.json()
            except Exception:
                rj = None
            code = rj.get('code') if isinstance(rj, dict) else None
            msg = rj.get('msg') if isinstance(rj, dict) else None
            apply_post(post, rj, vars_)
            ok = status == 200
            flag = 'OK ' if ok else 'ERR'
            extra = f" code={code}" if code is not None else ''
            print(f"[{flag}] {status}{extra} | {title}")
            if not ok or (isinstance(code, int) and code != 200):
                print(f"       msg={msg}  body={str(rj)[:300]}")
            results.append((title, status, code, msg))
        except Exception as e:
            print(f"[EXC] {title}: {e}")
            results.append((title, None, None, str(e)))
    return results, vars_

if __name__ == '__main__':
    path = sys.argv[1]
    filter_re = sys.argv[2] if len(sys.argv) > 2 else '.*'
    run(path, filter_re)
