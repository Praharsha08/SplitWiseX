# Console Flows

## Main Menu
1) Add user
2) Add expense
3) Show balances
4) Show suggested settlements
5) Exit

## Add User (Flow)
- Prompt: "Enter user name:"
- Validate non-empty & unique (case-insensitive)
- On success: "User added: <name>"

## Add Expense (Flow)
- Prompt payer name → validate exists
- Prompt amount (e.g., "12.30") → parse to Money, must be > 0
- Prompt participants (comma-separated names) → validate all exist & non-empty
- Prompt date (YYYY-MM-DD) → parse to LocalDate
- Prompt description (optional)
- On success: "Expense recorded."

## Show Balances (Flow)
- Print table: User | Balance (positive=receive, negative=owe)

## Show Suggested Settlements (Flow)
- Calculate balances
- Run greedy
- Print list: "<from> pays <to>: <amount>"
