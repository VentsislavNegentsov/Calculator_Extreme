# Implementation Plan - Fix Hexadecimal Parsing and Bitwise Operators

The user reported a bug where hexadecimal expressions like `EB-EA` result in an error. This is caused by the current `MathEvaluator` only supporting single-character hex digits and missing support for multi-character hex numbers. Additionally, several bitwise operators used in Programmer mode (`XOR`, `<<`, `>>`) are either incorrectly implemented or missing entirely.

## User Review Required

> [!IMPORTANT]
> I will be updating the `MathEvaluator` to assume **Base 16 (Hexadecimal)** for ALL numbers entered while in **Programmer Mode**.
> - `10` will be interpreted as `16` (Hex 10).
> - `EB` will be interpreted as `235`.
>
> This is a behavior change for decimal numbers in Programmer mode, but it is necessary to support hexadecimal input consistently without requiring prefixes like `0x`.

## Proposed Changes

### 1. [MathEvaluator](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)

- **Update `evaluate` signature**: Add `isHex: Boolean = false` parameter.
- **Support Multi-character Hex**: Update the number parsing logic to include `A-F` when `isHex` is true.
- **Support Bitwise Operators**:
    - Add `eatString(s: String)` to handle `<<` and `>>`.
    - Implement correct bitwise precedence levels: `Shift` (`<<`, `>>`), `AND` (`&`), `XOR` (`^`), and `OR` (`|`).
- **Fix XOR vs Power**: Ensure `^` is bitwise XOR in Programmer mode and `pow()` in other modes.

### 2. [AdvancedCalculatorScreen](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)

- **Update `calculateResult`**: Pass `currentMode == CalcMode.PROGRAMMER` as the `isHex` argument to `MathEvaluator.evaluate`.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to ensure compilation success.

### Manual Verification
- **Hex Arithmetic**: `EB - EA` should equal `1`.
- **Bitwise Shifts**: `1 << 4` should equal `10` (Hex).
- **Bitwise Logic**: `F & 7` should equal `7`, `1 ^ 1` should equal `0`.
- **Regression**: `2 ^ 3` in Scientific mode should still equal `8`.
