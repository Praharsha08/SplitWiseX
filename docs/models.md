# Domain Model (Signatures & Invariants)

## User
**Fields**
- `name: String` (unique within a Group)
- `[email: String]` (optional)

**Invariants**
- `name` non-empty, trimmed
- Case-insensitive uniqueness inside a Group (decision)

**Methods (signatures only)**
- Accessors
- `equals/hashCode` by `name` (decision)

---

## Expense
**Fields**
- `payer: User`
- `amount: Money` (see ADR-001)
- `participants: List<User>` (non-empty)
- `date: LocalDate`
- `description: String`

**Invariants**
- `amount > 0`
- `payer` ∈ Group.members
- `participants ⊆ Group.members` and not empty

**Methods (signatures only)**
- Accessors

---

## Group
**Purpose**  
Represents a logical container of users and their shared expenses.  
Holds only domain data (members, expenses) and simple mutations; **no business calculations** (balances/settlements) — those belong in `ExpenseManager`.

**Fields**
- `name: String` — group display name
- `members: List<User>` — list of unique users (unique by name within the group)
- `expenses: List<Expense>` — list of recorded expenses

**Invariants**
- `name` is non-empty, trimmed.
- Usernames are unique within the group (case-insensitive).
- Every `Expense` references only users that are members of this group.
- Exposed member/expense collections must be read-only (e.g., unmodifiable views) to preserve encapsulation.

**Methods (signatures only)**
- `Group(name: String)` — constructor
- `getName(): String`
- `getMembers(): List<User>` — returns an unmodifiable list
- `getExpenses(): List<Expense>` — returns an unmodifiable list
- `rename(newName: String): void` — updates group name (validates non-empty)
- `addMember(member: User): void` — adds if not already present (case-insensitive uniqueness)
- `addExpense(expense: Expense): void` — assumes expense is valid for this group
- `findMemberByName(name: String): User | null` — convenience lookup
- `[hasMember(name: String): boolean]` — convenience check (optional)

**Validation & Error Handling**
- `addMember`: reject duplicates (by normalized name).
- `addExpense`: may assume pre-validation by `ExpenseManager`, or optionally verify here that
  `expense.payer` and all `expense.participants` exist in `members`.  
  Choose one source of truth and document it to avoid double-validation.

**Notes**
- Keep `Group` as a *pure domain entity*: it should not print to console or read input.
- Prefer `List` in field types (not `ArrayList`) for flexibility; initialize with concrete lists internally.
- Consider marking internal lists as `final` and exposing only read-only views.

---

## Transaction (Suggested Settlement)
**Fields**
- `from: User`
- `to: User`
- `amount: Money`

**Notes**
- Represents *suggested* settlement, not an executed payment.

---

## ExpenseManager (logic)
**Responsibilities**
- Validation, balances, settlement proposal

**Key Methods (signatures only)**
- `addUser(group, name): void`
- `addExpense(group, payerName, amount, participantsNames, date, description): void`
- `Map<User, Money> calculateBalances(group): ...`
- `List<Transaction> suggestSettlements(balances): ...`

**Error Handling**
- Throw/return clear errors on: unknown user, empty participants, non-positive amounts

---

## Money
**Purpose**  
Represents a precise monetary value with a fixed currency.  
Used instead of floating-point numbers to avoid rounding errors and to ensure consistency in all financial calculations.

**Fields**
- `amount: long` — stored in the smallest unit (e.g., 1230 = 12.30 ₪)
- `currency: String` — ISO 4217 currency code such as `"ILS"` or `"USD"` (optional in MVP)

**Invariants**
- `currency` must match in all arithmetic operations (`add`, `subtract`, etc.)
- Values are immutable — every operation returns a new `Money` instance.
- Negative amounts are allowed only to represent debts.
