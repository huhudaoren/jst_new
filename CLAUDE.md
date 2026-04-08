# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository nature

This repository contains **no code** — only Chinese-language product requirement documents (PRD) for 竞赛通 (JingSaiTong), a competition-services platform. All files live under `需求文档/`. There is no build, no tests, no package manager.

## Product overview (V4.0 — independent delivery baseline)

竞赛通 is a one-stop competition platform for students, parents, channel partners (teachers/agencies), event organizers, and platform admins. Current focus is cultural & arts competitions ("find / register / view results / certificates / courses"). V4.0 (dated 2026-03-31) is a **clean re-development baseline**: V3.0 and earlier are explicitly deprecated and must not be treated as functional, role, or data references.

V4.0 adds: channel rebate & settlement system, points/level system + points mall, event-organizer self-onboarding with an independent web workbench, and corrects role-model mistakes from older versions.

## Terminal forms (architecture surface)

- WeChat Mini Program — students / parents / channel partners
- 赛事方 Web 工作台 — event organizer workbench
- 平台管理 Web 端 — platform admin backend
- 平台 H5 审核端 — H5 reviewer
- 赛事方公开申请页 — public organizer application page

## Document set — the V4.0 delivery baseline

These files in `需求文档/` form the **only** authoritative spec. When answering questions or drafting changes, cross-check across them; business-rule and data-field consistency wins over any single doc.

- `竞赛通-产品需求文档-V4.0.md` — main PRD
- `竞赛通-管理后台Web端需求文档-V4.0.md` — admin backend PRD
- `竞赛通-业务状态机与字典附录-V4.0.md` — state machines & enums
- `竞赛通-数据模型附录-V4.0.md` — data model
- `竞赛通-接口与第三方集成附录-V4.0.md` — APIs & 3rd-party integrations
- `竞赛通-页面验收附录-V4.0.md` — page acceptance criteria
- `竞赛通-终端形态与架构规划说明-V4.0.md` — terminal forms & architecture

Older versions kept for history only:
- `竞赛通-产品需求文档-V2.1.md`, `V3.0.md` — **deprecated**, do not use as a basis for changes.

## Working in this repo

- Edits are documentation edits. Preserve the Chinese tone, table formatting, and section numbering already in use.
- When a change touches business rules, status values, fields, or APIs, update **all** affected appendix docs in the same pass — the docs are designed to be consistent with each other.
- Do not reintroduce concepts from V3.0 or earlier unless explicitly asked.
