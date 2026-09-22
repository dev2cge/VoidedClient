# VoidedClient 1.12.0 — Companion Overhaul + Wave 5E

## Companion 2.0
- Completely rebuilt the Companion layout around one consistent shell.
- Main navigation is now a clean 5x2 grid instead of one cramped horizontal strip.
- All screens use the same header, content panel and fixed footer.
- Refresh/Close controls stay in fixed footer positions and do not cover content.
- Cards, spacing, headings and colors are now consistent between tabs.

## Dimension navigation
- Explore now uses `Overview` / `Daily Contract`.
- Nether now uses matching `Overview` / `Daily Contract` sub-tabs.
- End now uses matching `Overview` / `Daily Contract` sub-tabs.
- Nether and End contracts reuse the server-authoritative state already supplied by VoidedCore, so no duplicate client state is invented.

## Wave 5E Market
- Added a native Market Companion page.
- Shows network listing count, own listing count, BUY/SELL split, recent shop income, spend, fees and transactions.
- Shows up to five of the player's current physical shop listings.
- `Open Market` jumps into VoidedCore's full `/market` GUI.
- Added `market-companion-v1`, `market.companion` and `market.open`.

Client version is now 1.12.0.
