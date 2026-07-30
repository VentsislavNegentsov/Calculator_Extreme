# Walkthrough - Relative Percentage Logic Fix

I have updated the calculator's math engine to support the standard "consumer calculator" logic for percentages.

## Changes Made

### Math Engine Refinement

#### [MainActivity.kt](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)
- **Implemented Relative Percentages**: Updated `MathEvaluator` to pass the preceding total (the "base") to the terms following a `+` or `-` operator.
- **Context-Aware Scaling**: In `parseFactor`, the percentage operator (`%`) now checks if it has a base value. If it does, it calculates the percentage **of that base** instead of just dividing by 100.
- **Maintained Postfix Consistency**: Operations like `*` and `/` (multiplication and division) still treat `%` as a simple `x / 100` postfix operator, which is the expected behavior for those cases.

## Verification Results

### Manual Verification Recommended
- **Addition**: Type `100 + 5%`. The result should now be `= 105`.
- **Subtraction**: Type `100 - 5%`. The result should now be `= 95`.
- **Multiplication**: Type `100 * 5%`. The result should correctly be `= 5`.
- **Chaining**: Type `100 + 50 + 10%`. The result should correctly be `150 + (10% of 150) = 165`.
