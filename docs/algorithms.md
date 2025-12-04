# Algorithms (Pseudocode)

## Money Representation
See ADR-001. All arithmetic uses either:
- `BigDecimal(scale=2, RoundingMode.HALF_UP)`, or
- integer cents (`long`).

### Helper
- `splitEqual(amount, n)` → per-head share with deterministic rounding.
- If integer cents: `perHead = round(amount / n)`; track remainder if needed.

---

## Calculate Balances (Equal Split)

**Idea**  
Each user gets a net balance:
- Positive → should receive
- Negative → owes

**Pseudocode**
```text
function calculateBalances(group):
    balances = map<User, Money>(default 0)

    for each expense in group.expenses:
        n = size(expense.participants)
        perHead = splitEqual(expense.amount, n)

        # payer paid full amount → credit
        balances[payer] += expense.amount

        # each participant owes perHead
        for each p in expense.participants:
            balances[p] -= perHead

    # Normalize near-zero (to 0) within epsilon (e.g., 1 cent)
    for each u in balances:
        if abs(balances[u]) < epsilon:
            balances[u] = 0

    return balances
```
---
## Settlement Suggestion (Greedy)
### Goal
Minimize number of transactions by matching the largest debtor to the largest creditor iteratively.

**Pseudocode**
```text
function suggestSettlements(balances):
    debtors = list of (user, amount) where amount < 0  # store as positive |amount|
    creditors = list of (user, amount) where amount > 0

    sort debtors descending by amount
    sort creditors descending by amount

    settlements = []

    i = 0
    j = 0
    while i < len(debtors) and j < len(creditors):
        d = debtors[i].amount
        c = creditors[j].amount
        pay = min(d, c)

        settlements.add( Transaction(from=debtors[i].user, to=creditors[j].user, amount=pay) )

        debtors[i].amount -= pay
        creditors[j].amount -= pay

        if debtors[i].amount == 0:
            i += 1
        if creditors[j].amount == 0:
            j += 1

    return settlements
```

### Notes
* Use epsilon for comparisons (1 cent tolerance).
* This greedy algorithm is adequate for MVP; advanced optimization can be added later.

---

## Validation Rules (Console)
* Reject empty usernames.
* Reject ```amount ≤ 0.```
* Payer and participants must exist in the group.
* Participants list must be non-empty.
* For equal split:
* * If rounding leaves a remainder (e.g., 1 cent), let the payer absorb the remainder or distribute the extra cent to the first k participants.
* * Document which rule you use in ADR-001.