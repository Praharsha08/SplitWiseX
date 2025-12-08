# ADR-001: Money Representation

## Context
Floating-point (`double`) introduces rounding errors in financial calculations.

## Decision
Use integer cents (`long`) **or** `BigDecimal(scale=2, RoundingMode.HALF_UP)` for all amounts.

## Consequences
- Deterministic arithmetic and output
- Need helpers: parse ("12.30" -> 1230), format (1230 -> "12.30")
- Rounding rule for equal split documented in algorithms.md
