package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isRetroMode by remember { mutableStateOf(false) }
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isRetroMode) Color.Black else MaterialTheme.colorScheme.background
                ) {
                    AdvancedCalculatorScreen(isRetroMode, onToggleRetro = { isRetroMode = it })
                }
            }
        }
    }
}

enum class CalcMode { BASIC, SCIENTIFIC, PROGRAMMER }

@Composable
fun AdvancedCalculatorScreen(isRetroMode: Boolean, onToggleRetro: (Boolean) -> Unit) {
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var currentMode by remember { mutableStateOf(CalcMode.SCIENTIFIC) }
    var isDeg by remember { mutableStateOf(true) }

    // Evaluates expression in real-time as user types
    fun calculateResult() {
        if (expression.isBlank()) {
            resultText = "0"
            return
        }
        val cleanExpr = expression.removeSuffix("=")
        if (cleanExpr.isBlank()) {
            resultText = "0"
            return
        }
        try {
            val evalResult = MathEvaluator.evaluate(cleanExpr, isDeg, currentMode == CalcMode.PROGRAMMER)
            resultText = if (evalResult % 1.0 == 0.0 && !evalResult.isInfinite()) {
                evalResult.toLong().toString(if (currentMode == CalcMode.PROGRAMMER) 16 else 10).uppercase()
            } else {
                evalResult.toString()
            }
        } catch (e: Exception) {
            resultText = "Error"
        }
    }

    fun appendInput(value: String) {
        expression += value
        calculateResult()
    }

    fun deleteLastChar() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            calculateResult()
        }
    }

    fun clearAll() {
        expression = ""
        resultText = "0"
    }

    val numericValue = resultText.toLongOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Caption
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Calculator Extreme v1.2 by Ventsislav Negentsov",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRetroMode) Color(0xFF33FF33) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mode & Theme Selector Tabs
            MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val totalButtons = CalcMode.values().size + 1
                
                // Mode Buttons
                CalcMode.values().forEachIndexed { index, mode ->
                    SegmentedButton(
                        checked = currentMode == mode,
                        onCheckedChange = { currentMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = totalButtons),
                        icon = {},
                        colors = if (isRetroMode) SegmentedButtonDefaults.colors(
                            activeContainerColor = Color(0xFF222222),
                            activeContentColor = Color(0xFF33FF33),
                            inactiveContainerColor = Color.Black,
                            inactiveContentColor = Color.Gray,
                            activeBorderColor = Color(0xFF33FF33),
                            inactiveBorderColor = Color.DarkGray
                        ) else SegmentedButtonDefaults.colors()
                    ) {
                        Text(mode.name, fontSize = 10.sp)
                    }
                }
                
                // Retro Toggle Button
                SegmentedButton(
                    checked = isRetroMode,
                    onCheckedChange = { onToggleRetro(it) },
                    shape = SegmentedButtonDefaults.itemShape(index = totalButtons - 1, count = totalButtons),
                    icon = {},
                    colors = if (isRetroMode) SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF222222),
                        activeContentColor = Color(0xFF33FF33),
                        inactiveContainerColor = Color.Black,
                        inactiveContentColor = Color.Gray,
                        activeBorderColor = Color(0xFF33FF33),
                        inactiveBorderColor = Color.DarkGray
                    ) else SegmentedButtonDefaults.colors()
                ) {
                    Text("RETRO", fontSize = 10.sp)
                }
            }
        }

        // Display Screen Area
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRetroMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isRetroMode) BorderStroke(2.dp, Color(0xFF33FF33)) else null
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Expression Text
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 24.sp,
                    color = if (isRetroMode) Color(0xFF33FF33).copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = if (isRetroMode) FontFamily.Monospace else FontFamily.Default
                )

                // Result Text
                Text(
                    text = "= $resultText",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRetroMode) Color(0xFF33FF33) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = if (isRetroMode) FontFamily.Monospace else FontFamily.Default
                )

                // Programmer Base Bar (Live DEC, HEX, BIN, OCT values)
                if (currentMode == CalcMode.PROGRAMMER) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = if (isRetroMode) Color(0xFF33FF33).copy(alpha = 0.3f) else DividerDefaults.color
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BaseInfo("HEX", numericValue?.toString(16)?.uppercase() ?: "-", isRetroMode)
                        BaseInfo("DEC", numericValue?.toString(10) ?: "-", isRetroMode)
                        BaseInfo("OCT", numericValue?.toString(8) ?: "-", isRetroMode)
                        BaseInfo("BIN", numericValue?.toString(2) ?: "-", isRetroMode)
                    }
                }
            }
        }

        // Keypads
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            when (currentMode) {
                CalcMode.BASIC -> BasicKeypad(::appendInput, ::deleteLastChar, ::clearAll, isRetroMode)
                CalcMode.SCIENTIFIC -> ScientificKeypad(
                    isDeg = isDeg,
                    onToggleDeg = { isDeg = !isDeg },
                    onAppend = ::appendInput,
                    onDelete = ::deleteLastChar,
                    onClear = ::clearAll,
                    isRetroMode = isRetroMode
                )
                CalcMode.PROGRAMMER -> ProgrammerKeypad(::appendInput, ::deleteLastChar, ::clearAll, isRetroMode)
            }
        }
    }
}

@Composable
fun BaseInfo(label: String, value: String, isRetroMode: Boolean) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRetroMode) Color(0xFF33FF33) else MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = if (isRetroMode) Color(0xFF33FF33) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// --- KEYPAD LAYOUTS ---

@Composable
fun ScientificKeypad(
    isDeg: Boolean,
    onToggleDeg: () -> Unit,
    onAppend: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    isRetroMode: Boolean
) {
    val rows = listOf(
        listOf("DEG/RAD", "sin", "cos", "tan", "AC"),
        listOf("atan", "sqrt", "log", "ln", "DEL"),
        listOf("(", ")", "^", "π", "/"),
        listOf("7", "8", "9", "e", "*"),
        listOf("4", "5", "6", "%", "-"),
        listOf("1", "2", "3", ".", "+"),
        listOf("0", "00", "000", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 2f else 1f
                CalculatorButton(
                    symbol = if (btn == "DEG/RAD") (if (isDeg) "DEG" else "RAD") else btn,
                    modifier = Modifier.weight(weight).height(48.dp),
                    isRetroMode = isRetroMode,
                    onClick = {
                        when (btn) {
                            "AC" -> onClear()
                            "DEL" -> onDelete()
                            "DEG/RAD" -> onToggleDeg()
                            "sin", "cos", "tan", "atan", "sqrt", "log", "ln" -> onAppend("$btn(")
                            else -> onAppend(btn)
                        }
                    }
                )
                if (btn == "=") return@Row
            }
        }
    }
}

@Composable
fun BasicKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit, isRetroMode: Boolean) {
    val rows = listOf(
        listOf("AC", "(", ")", "/", "DEL"),
        listOf("7", "8", "9", "*", "^"),
        listOf("4", "5", "6", "-", "%"),
        listOf("1", "2", "3", "+", "."),
        listOf("0", "00", "000", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 2f else 1f
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(weight).height(54.dp),
                    isRetroMode = isRetroMode,
                    onClick = {
                        when (btn) {
                            "AC" -> onClear()
                            "DEL" -> onDelete()
                            else -> onAppend(btn)
                        }
                    }
                )
                if (btn == "=") return@Row
            }
        }
    }
}

@Composable
fun ProgrammerKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit, isRetroMode: Boolean) {
    val rows = listOf(
        listOf("A", "B", "C", "AC", "DEL"),
        listOf("D", "E", "F", "/", "*"),
        listOf("7", "8", "9", "-", "AND"),
        listOf("4", "5", "6", "+", "OR"),
        listOf("1", "2", "3", "XOR", "NOT"),
        listOf("0", "(", ")", "<<", ">>"),
        listOf("=", "=", "=", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 5f else 1f
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(weight).height(48.dp),
                    isRetroMode = isRetroMode,
                    onClick = {
                        when (btn) {
                            "AC" -> onClear()
                            "DEL" -> onDelete()
                            "AND" -> onAppend("&")
                            "OR" -> onAppend("|")
                            "XOR" -> onAppend("^")
                            "NOT" -> onAppend("~")
                            "=" -> onAppend("=") // calculateResult is called inside appendInput
                            else -> onAppend(btn)
                        }
                    }
                )
                if (btn == "=") return@Row // Only draw one '=' button for the whole row
            }
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, modifier: Modifier, isRetroMode: Boolean, onClick: () -> Unit) {
    val isOperator = symbol in listOf("+", "-", "*", "/", "^", "%", "=", "&", "|", "~", "<<", ">>")
    val isSpecial = symbol in listOf("AC", "DEL")

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isRetroMode -> Color(0xFF222222)
                isSpecial -> MaterialTheme.colorScheme.errorContainer
                isOperator -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = when {
                isRetroMode -> when {
                    isSpecial -> Color(0xFFFF9900) // Fluorescent Orange
                    isOperator -> Color(0xFFFFFF00) // Fluorescent Yellow
                    else -> Color(0xFF33FF33) // Fluorescent Green
                }
                isSpecial -> MaterialTheme.colorScheme.onErrorContainer
                isOperator -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        ),
        border = if (isRetroMode) BorderStroke(1.dp, Color(0xFF33FF33).copy(alpha = 0.2f)) else null
    ) {
        Text(
            text = symbol,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isRetroMode) FontFamily.Monospace else FontFamily.Default
        )
    }
}

// --- PURE KOTLIN EXPRESSION PARSER / EVALUATOR ---

object MathEvaluator {
    fun evaluate(expression: String, isDeg: Boolean = true, isHex: Boolean = false): Double {
        var expr = expression
            .replace("π", PI.toString())
            .replace("e", E.toString())
            .replace("×", "*")
            .replace("÷", "/")

        var pos = -1
        var ch = -1

        fun nextChar() {
            ch = if (++pos < expr.length) expr[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun eatString(s: String): Boolean {
            while (ch == ' '.code) nextChar()
            if (pos + s.length <= expr.length && expr.substring(pos, pos + s.length) == s) {
                pos += s.length - 1
                nextChar()
                return true
            }
            return false
        }

        val parser = object {
            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                if (eat('~'.code)) return parseFactor().toLong().inv().toDouble()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch in '0'.code..'9'.code) || ch == '.'.code || (isHex && ch in 'A'.code..'F'.code)) {
                    while ((ch in '0'.code..'9'.code) || ch == '.'.code || (isHex && ch in 'A'.code..'F'.code)) nextChar()
                    val s = expr.substring(startPos, pos)
                    x = if (isHex) s.toLong(16).toDouble() else s.toDouble()
                } else if (ch in 'a'.code..'z'.code) {
                    while (ch in 'a'.code..'z'.code) nextChar()
                    val name = expr.substring(startPos, pos)
                    x = parseFactor()
                    val rad = if (isDeg) Math.toRadians(x) else x
                    x = when (name) {
                        "sqrt" -> sqrt(x)
                        "sin" -> sin(rad)
                        "cos" -> cos(rad)
                        "tan" -> tan(rad)
                        "atan" -> if (isDeg) Math.toDegrees(atan(x)) else atan(x)
                        "log" -> log10(x)
                        "ln" -> ln(x)
                        else -> throw RuntimeException("Unknown function: $name")
                    }
                } else {
                    throw RuntimeException("Unexpected character: ${ch.toChar()}")
                }

                if (!isHex && eat('^'.code)) x = x.pow(parseFactor())

                return x
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else if (eat('%'.code)) x %= parseFactor()
                    else return x
                }
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseBitwise(): Double {
                var x = parseExpression()
                while (true) {
                    if (eatString("<<")) x = (x.toLong() shl parseExpression().toInt()).toDouble()
                    else if (eatString(">>")) x = (x.toLong() shr parseExpression().toInt()).toDouble()
                    else if (eat('&'.code)) x = (x.toLong() and parseExpression().toLong()).toDouble()
                    else if (eat('^'.code) && isHex) x = (x.toLong() xor parseExpression().toLong()).toDouble()
                    else if (eat('|'.code)) x = (x.toLong() or parseExpression().toLong()).toDouble()
                    else return x
                }
            }
        }

        nextChar()
        val result = if (isHex) parser.parseBitwise() else parser.parseExpression()
        if (pos < expr.length) throw RuntimeException("Unexpected: " + expr[pos])
        return result
    }
}