# Smart Expense Splitter - Project Plan

# Project overview

## Project goals

### Core Features

- **Expense Entry:** Allow users to quickly add expenses with amount, description, and date
- **Group Management:** Create and manage groups of people who share expenses
- **Smart Splitting:** Automatically calculate how much each person owes or is owed
- **Settlement Tracking:** Record payments between group members to settle debts
- **Balance Overview:** Display clear summary of who owes what to whom

### MVP Constraints

- Single currency support only
- Equal splits by default (custom splits can be added post-MVP)
- Basic user authentication
- Mobile-responsive web app (no native apps in MVP)
- No receipt scanning or image uploads

### Success Metrics

- Users can add an expense and split it within 30 seconds
- Settlement calculations are accurate 100% of the time
- App is usable on both desktop and mobile browsers

## Project phase: Console app

This version will focus on the **core algorithmic logic** and data flow, without UI or authentication.

The goal is to validate the calculation and optimization engine before adding GUI or web functionality.

## Data Model Overview

| **Entity** | **Key Attributes** | **Relationships** | **Purpose** |
| --- | --- | --- | --- |
| **User** | id, name, email | Member of multiple Groups; Creator of Expenses; Participant in Expenses | Represents a person who can create groups, add expenses, and owe/be owed money |
| **Group** | id, name, created_date | Has multiple Users as members; Contains multiple Expenses | Container for shared expenses among a set of users |
| **Expense** | id, amount, description, date, paid_by_user_id | Belongs to one Group; Paid by one User; Split among multiple Users via ExpenseSplit | Records a single expense that needs to be split among group members |
| **ExpenseSplit** | expense_id, user_id, share_amount | Links an Expense to a User with their share amount | Tracks how much each user owes for a specific expense |
| **Settlement** | id, from_user_id, to_user_id, amount, date, group_id | Between two Users within a Group | Records when one user pays another to settle debts |

## Algorithm Concept

The settlement algorithm calculates each user’s net balance and matches the largest debtor with the largest creditor iteratively until all balances are zeroed. This ensures minimal transactions.

## Future Enhancements

- JavaFX GUI for interactive input
- JSON data persistence
- Multi-currency and receipt upload
- Graph optimization for fewer settlements

# ✅ Smart Expense Splitter — Task List (Console MVP)

> Phase: Console Prototype
> 
> 
> Goal: Validate core logic (balances + settlements) before GUI/Web
> 

---

## 🧱 M0 — Project Skeleton

- [ ]  Initialize Java project (Gradle/Maven or plain)
- [ ]  Create `Main` entry point
- [ ]  Create `logic` package and `ExpenseManager` shell
- [ ]  Add MONEY helper (BigDecimal with scale=2 **or** amounts in cents as `long`)

---

## 👤 M1 — Domain Model (Minimal)

- [ ]  Implement `User` (name[, email optional])
- [ ]  Implement `Group` (name, members, expenses)
- [ ]  Implement `Expense` (payer, amountCents/BigDecimal, participants, date, description)
- [ ]  (Skip IDs for now; unique name per group is enough)
- [ ]  Use `LocalDate`/`LocalDateTime` for dates

---

## 🧮 M2 — Recording & Balances

- [ ]  Add users to group (with validation: no duplicates, non-empty names)
- [ ]  Add expense to group (payer exists, participants exist, non-empty participants)
- [ ]  Implement **equal split** calculation (handle rounding)
- [ ]  Compute balances: `Map<User, Amount>` (positive = to receive, negative = owes)
- [ ]  Pretty-format money outputs (e.g., `₪12.30`)

---

## 🤝 M3 — Settlements (Greedy)

- [ ]  Implement `Transaction(from, to, amount)` (suggested settlements)
- [ ]  Greedy algorithm: match largest debtor ↔ largest creditor until balanced
- [ ]  Ensure termination & correctness (zero out near-zero with tolerance)
- [ ]  Expose method: `List<Transaction> suggestSettlements(Map<User, Amount>)`

---

## 💻 M4 — Console UI

- [ ]  Console menu: **Add User**, **Add Expense**, **Show Balances**, **Show Suggested Settlements**, **Exit**
- [ ]  Input parsing + prompts (payer, amount, participants, date, description)
- [ ]  Output tables aligned (names, amounts), readable summaries
- [ ]  Separate UI from logic (`Main` handles I/O; `ExpenseManager` handles calculations)

---

## 🧪 M5 — Tests & Edge Cases

- [ ]  Seed sample data (≥3 users, ≥6 expenses with varied participants)
- [ ]  Edge: zero amount, single participant, unknown payer/participant, empty group
- [ ]  Edge: non-divisible splits (rounding consistency end-to-end)
- [ ]  Edge: all-zero balances, already balanced group
- [ ]  Input validation & friendly error messages

---

## ✨ M6 — Polish & Docs

- [ ]  Inline comments explaining the settlement algorithm + rounding decisions
- [ ]  `README.md`: project purpose, how to run, sample session, algorithm notes
- [ ]  Example output (balances + suggested settlements)
- [ ]  (Optional) `Settlement` record to log **actual** payments performed
- [ ]  (Optional) JSON save/load for group data

---

## 🧷 Quick Backlog / Stretch

- [ ]  Custom split per expense (re-enable `ExpenseSplit` structure)
- [ ]  Categories + reporting (per-user/per-category stats)
- [ ]  JavaFX GUI (TableView, PieChart, “Optimize Transfers” button)
- [ ]  Multi-currency (manual rate input), receipts (future)