# Architecture Overview (Console MVP)

## Goals
- Separate domain (models), logic (calculations), and UI (console I/O).
- Keep logic testable and UI-thin.

## Packages (initial)
- `models` — domain entities: `User`, `Group`, `Expense`, `Transaction`
- `logic` — orchestration & calculations: `ExpenseManager`
- `ui` — console interaction (menu, input parsing)
- `util` (optional) — money formatting, date helpers

## Responsibilities
- `ExpenseManager`:
    - Validate inputs (user/expense membership)
    - Compute balances per user
    - Suggest settlement transactions (greedy)
- `ui`:
    - Read user input, call `ExpenseManager`, print results
    - No business logic

## Data Persistence (MVP)
- None. In-memory only. (Future: JSON save/load)

## Non-Functional Constraints
- Deterministic money arithmetic
- Clear error messages for invalid input
