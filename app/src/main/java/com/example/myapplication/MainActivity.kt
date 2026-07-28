package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdvancedCalculatorScreen()
                }
            }
        }
    }
}

enum class CalcMode { BASIC, SCIENTIFIC, PROGRAMMER }

@Composable
fun AdvancedCalculatorScreen() {
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
        try {
            val evalResult = MathEvaluator.evaluate(expression, isDeg)
            resultText = if (evalResult % 1.0 == 0.0 && !evalResult.isInfinite()) {
                evalResult.toLong().toString()
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
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Caption
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Calculator by Ventsislav Negentsov",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mode Selector Tabs
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CalcMode.values().forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = currentMode == mode,
                        onClick = { currentMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                    ) {
                        Text(mode.name.take(4), fontSize = 12.sp)
                    }
                }
            }
        }

        // Display Screen Area
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Expression Text
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                // Result Text
                Text(
                    text = "= $resultText",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                // Programmer Base Bar (Live DEC, HEX, BIN, OCT values)
                if (currentMode == CalcMode.PROGRAMMER) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BaseInfo("HEX", numericValue?.toString(16)?.uppercase() ?: "-")
                        BaseInfo("DEC", numericValue?.toString(10) ?: "-")
                        BaseInfo("OCT", numericValue?.toString(8) ?: "-")
                        BaseInfo("BIN", numericValue?.toString(2) ?: "-")
                    }
                }
            }
        }

        // Keypads
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            when (currentMode) {
                CalcMode.BASIC -> BasicKeypad(::appendInput, ::deleteLastChar, ::clearAll)
                CalcMode.SCIENTIFIC -> ScientificKeypad(
                    isDeg = isDeg,
                    onToggleDeg = { isDeg = !isDeg },
                    onAppend = ::appendInput,
                    onDelete = ::deleteLastChar,
                    onClear = ::clearAll
                )
                CalcMode.PROGRAMMER -> ProgrammerKeypad(::appendInput, ::deleteLastChar, ::clearAll)
            }
        }
    }
}

@Composable
fun BaseInfo(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = value, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

// --- KEYPAD LAYOUTS ---

@Composable
fun ScientificKeypad(
    isDeg: Boolean,
    onToggleDeg: () -> Unit,
    onAppend: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    val rows = listOf(
        listOf("DEG/RAD", "sin", "cos", "tan", "C"),
        listOf("atan", "sqrt", "log", "ln", "DEL"),
        listOf("(", ")", "^", "π", "/"),
        listOf("7", "8", "9", "e", "*"),
        listOf("4", "5", "6", "%", "-"),
        listOf("1", "2", "3", ".", "+"),
        listOf("0", "00", "=", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 3f else 1f
                CalculatorButton(
                    symbol = if (btn == "DEG/RAD") (if (isDeg) "DEG" else "RAD") else btn,
                    modifier = Modifier.weight(weight).height(48.dp),
                    onClick = {
                        when (btn) {
                            "C" -> onClear()
                            "DEL" -> onDelete()
                            "DEG/RAD" -> onToggleDeg()
                            "sin", "cos", "tan", "atan", "sqrt", "log", "ln" -> onAppend("$btn(")
                            else -> onAppend(btn)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BasicKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit) {
    val rows = listOf(
        listOf("C", "(", ")", "/", "DEL"),
        listOf("7", "8", "9", "*", "^"),
        listOf("4", "5", "6", "-", "%"),
        listOf("1", "2", "3", "+", "="),
        listOf("0", ".", "=", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = {
                        when (btn) {
                            "C" -> onClear()
                            "DEL" -> onDelete()
                            else -> onAppend(btn)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProgrammerKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit) {
    val rows = listOf(
        listOf("A", "B", "C", "C", "DEL"),
        listOf("D", "E", "F", "/", "*"),
        listOf("7", "8", "9", "-", "AND"),
        listOf("4", "5", "6", "+", "OR"),
        listOf("1", "2", "3", "XOR", "NOT"),
        listOf("0", "(", ")", "<<", ">>")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {
                        when (btn) {
                            "C" -> onClear()
                            "DEL" -> onDelete()
                            "AND" -> onAppend("&")
                            "OR" -> onAppend("|")
                            "XOR" -> onAppend("^")
                            "NOT" -> onAppend("~")
                            else -> onAppend(btn)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, modifier: Modifier, onClick: () -> Unit) {
    val isOperator = symbol in listOf("+", "-", "*", "/", "^", "%", "=", "&", "|", "~", "<<", ">>")
    val isSpecial = symbol in listOf("C", "DEL")

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isSpecial -> MaterialTheme.colorScheme.errorContainer
                isOperator -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = when {
                isSpecial -> MaterialTheme.colorScheme.onErrorContainer
                isOperator -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        )
    ) {
        Text(text = symbol, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// --- PURE KOTLIN EXPRESSION PARSER / EVALUATOR ---

object MathEvaluator {
    fun evaluate(expression: String, isDeg: Boolean = true): Double {
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
                } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                    while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                    x = expr.substring(startPos, pos).toDouble()
                } else if (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) {
                    while (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) nextChar()
                    val name = expr.substring(startPos, pos)

                    // Check for Hexadecimal numbers (A-F) vs functions
                    if (name.length == 1 && name[0] in 'A'..'F') {
                        x = name.toInt(16).toDouble()
                    } else {
                        x = parseFactor()
                        val rad = if (isDeg) Math.toRadians(x) else x
                        x = when (name) {
                            "sqrt" -> sqrt(x)
                            "sin" -> sin(rad)
                            "cos" -> cos(rad)
                            "tan" -> tan(rad)
                            "atan" -> {
                                val res = atan(x)
                                if (isDeg) Math.toDegrees(res) else res
                            }
                            "log" -> log10(x)
                            "ln" -> ln(x)
                            else -> throw RuntimeException("Unknown function: $name")
                        }
                    }
                } else {
                    throw RuntimeException("Unexpected character")
                }

                if (eat('^'.code)) x = x.pow(parseFactor())

                return x
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else if (eat('%'.code)) x %= parseFactor()
                    else if (eat('&'.code)) x = (x.toLong() and parseFactor().toLong()).toDouble()
                    else if (eat('|'.code)) x = (x.toLong() or parseFactor().toLong()).toDouble()
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
        }

        nextChar()
        val result = parser.parseExpression()
        return result
    }
}