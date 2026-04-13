#!/usr/bin/env python3
"""Fix literal \\uXXXX Unicode escapes in Vue files."""
import glob
import re

files = glob.glob('RuoYi-Vue/ruoyi-ui/src/views/jst/**/*.vue', recursive=True)
files += glob.glob('RuoYi-Vue/ruoyi-ui/src/views/partner/**/*.vue', recursive=True)

# Pattern to match literal \uXXXX in bytes
pat = re.compile(rb'\\u([0-9a-fA-F]{4})')

file_count = 0
total_replacements = 0

for f in files:
    with open(f, 'rb') as fp:
        raw = fp.read()

    if b'\\u' not in raw:
        continue

    def decode_byte_match(m):
        cp = int(m.group(1), 16)
        if cp == 0x00A5:  # Keep ¥ currency symbol
            return m.group(0)
        return chr(cp).encode('utf-8')

    new_raw = pat.sub(decode_byte_match, raw)

    if new_raw != raw:
        with open(f, 'wb') as fp:
            fp.write(new_raw)
        count = len(pat.findall(raw)) - len(pat.findall(new_raw))
        total_replacements += count
        file_count += 1
        print(f'Fixed ({count} replacements): {f}')

print(f'\nTotal: {file_count} files, {total_replacements} replacements')

# Verify
remaining = 0
for f in files:
    with open(f, 'rb') as fp:
        raw = fp.read()
    for m in pat.finditer(raw):
        cp = int(m.group(1), 16)
        if cp != 0x00A5:
            remaining += 1
print(f'Remaining non-currency escapes: {remaining}')
