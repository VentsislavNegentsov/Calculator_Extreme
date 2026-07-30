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
            var themeIndex by remember { mutableStateOf(0) }
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (themeIndex > 0) Color.Black else MaterialTheme.colorScheme.background
                ) {
                    AdvancedCalculatorScreen(themeIndex, onThemeChange = { themeIndex = it })
                }
            }
        }
    }
}

enum class CalcMode { BASIC, SCIENTIFIC, PROGRAMMER }

data class RetroTheme(
    val name: String,
    val primary: Color,
    val special: Color,
    val operator: Color
)

val RetroThemes = listOf(
    RetroTheme("GREEN", Color(0xFF33FF33), Color(0xFFFF9900), Color(0xFFFFFF00)),
    RetroTheme("AMBER", Color(0xFFFFB000), Color(0xFFFF4400), Color(0xFFFFEE00)),
    RetroTheme("CYAN", Color(0xFF00FFFF), Color(0xFFFF55BB), Color(0xFF55FF55)),
    RetroTheme("PLASMA", Color(0xFFFF5500), Color(0xFF00FFCC), Color(0xFFFFFF33)),
    RetroTheme("BLUE", Color(0xFF0088FF), Color(0xFFFF3333), Color(0xFFCCFF00)),
    RetroTheme("PINK", Color(0xFFFF00FF), Color(0xFF00FFEE), Color(0xFFFFFF00))
)

@Composable
fun AdvancedCalculatorScreen(themeIndex: Int, onThemeChange: (Int) -> Unit) {
    val isRetroMode = themeIndex > 0
    val activeRetroTheme = if (isRetroMode) RetroThemes[themeIndex - 1] else null
    val themeColor = activeRetroTheme?.primary ?: MaterialTheme.colorScheme.primary
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var currentMode by remember { mutableStateOf(CalcMode.SCIENTIFIC) }
    var isDeg by remember { mutableStateOf(true) }
    var programmerRadix by remember { mutableStateOf(16) }

    // Evaluates expression in real-time as user types
    fun calculateResult() {
        if (expression.isBlank()) {
            resultText = "0"
            return
        }
        var cleanExpr = expression.removeSuffix("=")
        
        // Remove trailing operators for real-time calculation
        val operators = listOf("+", "-", "*", "/", "&", "|", "^", "<<", ">>", "(", "~", "<", ">")
        while (operators.any { cleanExpr.endsWith(it) } && cleanExpr.isNotEmpty()) {
            cleanExpr = cleanExpr.dropLast(1).trim()
        }

        if (cleanExpr.isBlank() || operators.any { cleanExpr == it }) {
            resultText = "0"
            return
        }
        
        try {
            val radix = if (currentMode == CalcMode.PROGRAMMER) programmerRadix else 10
            val evalResult = MathEvaluator.evaluate(cleanExpr, isDeg, radix)
            resultText = if (evalResult % 1.0 == 0.0 && !evalResult.isInfinite()) {
                evalResult.toLong().toString(radix).uppercase()
            } else {
                evalResult.toString()
            }
        } catch (e: Exception) {
            // Keep the previous result or show Error only if it's a real syntax error
            // instead of just an incomplete expression
            if (expression.endsWith("=")) resultText = "Error"
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

    val numericValue = if (currentMode == CalcMode.PROGRAMMER) resultText.toLongOrNull(programmerRadix) else resultText.toLongOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Caption
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isRetroMode) themeColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Calculator Extreme v1.3 by Ventsislav Negentsov",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRetroMode) themeColor else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

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
                            activeContentColor = themeColor,
                            inactiveContainerColor = Color.Black,
                            inactiveContentColor = Color.Gray,
                            activeBorderColor = themeColor,
                            inactiveBorderColor = Color.DarkGray
                        ) else SegmentedButtonDefaults.colors()
                    ) {
                        Text(mode.name, fontSize = 9.sp)
                    }
                }
                
                // Retro Toggle Button
                SegmentedButton(
                    checked = isRetroMode,
                    onCheckedChange = { onThemeChange((themeIndex + 1) % 7) },
                    shape = SegmentedButtonDefaults.itemShape(index = totalButtons - 1, count = totalButtons),
                    icon = {},
                    colors = if (isRetroMode) SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF222222),
                        activeContentColor = themeColor,
                        inactiveContainerColor = Color.Black,
                        inactiveContentColor = Color.Gray,
                        activeBorderColor = themeColor,
                        inactiveBorderColor = Color.DarkGray
                    ) else SegmentedButtonDefaults.colors()
                ) {
                    Text(if (isRetroMode) activeRetroTheme!!.name else "STYLE", fontSize = 9.sp)
                }
            }

            if (currentMode == CalcMode.PROGRAMMER) {
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val bases = listOf(16 to "HEX", 10 to "DEC", 8 to "OCT", 2 to "BIN")
                    bases.forEachIndexed { index, (r, label) ->
                        SegmentedButton(
                            selected = programmerRadix == r,
                            onClick = { 
                                programmerRadix = r
                                calculateResult()
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = 4),
                            icon = {},
                            colors = if (isRetroMode) SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFF222222),
                                activeContentColor = themeColor,
                                inactiveContainerColor = Color.Black,
                                inactiveContentColor = Color.Gray,
                                activeBorderColor = themeColor,
                                inactiveBorderColor = Color.DarkGray
                            ) else SegmentedButtonDefaults.colors()
                        ) {
                            Text(label, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Display Screen Area
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRetroMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isRetroMode) BorderStroke(2.dp, themeColor) else null
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Expression Text
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 24.sp,
                    color = if (isRetroMode) themeColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = if (isRetroMode) FontFamily.Monospace else FontFamily.Default
                )

                // Result Text
                Text(
                    text = "= $resultText",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRetroMode) themeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = if (isRetroMode) FontFamily.Monospace else FontFamily.Default,
                    lineHeight = 46.sp
                )

                // Programmer Base Bar (Live DEC, HEX, BIN, OCT values)
                if (currentMode == CalcMode.PROGRAMMER) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = if (isRetroMode) themeColor.copy(alpha = 0.3f) else DividerDefaults.color
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BaseInfo("HEX", numericValue?.toString(16)?.uppercase() ?: "-", themeIndex)
                        BaseInfo("DEC", numericValue?.toString(10) ?: "-", themeIndex)
                        BaseInfo("OCT", numericValue?.toString(8) ?: "-", themeIndex)
                        BaseInfo("BIN", numericValue?.toString(2) ?: "-", themeIndex)
                    }
                }
            }
        }

        // Keypads
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            when (currentMode) {
                CalcMode.BASIC -> BasicKeypad(::appendInput, ::deleteLastChar, ::clearAll, themeIndex)
                CalcMode.SCIENTIFIC -> ScientificKeypad(
                    isDeg = isDeg,
                    onToggleDeg = { isDeg = !isDeg },
                    onAppend = ::appendInput,
                    onDelete = ::deleteLastChar,
                    onClear = ::clearAll,
                    themeIndex = themeIndex
                )
                CalcMode.PROGRAMMER -> ProgrammerKeypad(::appendInput, ::deleteLastChar, ::clearAll, themeIndex)
            }
        }
    }
}

@Composable
fun BaseInfo(label: String, value: String, themeIndex: Int) {
    val isRetroMode = themeIndex > 0
    val activeRetroTheme = if (isRetroMode) RetroThemes[themeIndex - 1] else null
    val themeColor = activeRetroTheme?.primary ?: MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRetroMode) themeColor else MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = if (isRetroMode) themeColor else MaterialTheme.colorScheme.onSurface
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
    themeIndex: Int
) {
    val rows = listOf(
        listOf("sin", "cos", "tan", "AC", "DEL"),
        listOf("sqrt", "log", "ln", "(", ")"),
        listOf("atan", "π", "e", "^", "/"),
        listOf("7", "8", "9", "%", "*"),
        listOf("4", "5", "6", "DEG/RAD", "-"),
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
                    themeIndex = themeIndex,
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
fun BasicKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit, themeIndex: Int) {
    val rows = listOf(
        listOf("7", "8", "9", "AC", "DEL"),
        listOf("4", "5", "6", "^", "/"),
        listOf("1", "2", "3", "%", "*"),
        listOf("0", ".", "(", ")", "-"),
        listOf("00", "000", "+", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 2f else 1f
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(weight).height(54.dp),
                    themeIndex = themeIndex,
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
fun ProgrammerKeypad(onAppend: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit, themeIndex: Int) {
    val rows = listOf(
        listOf("A", "B", "C", "AC", "DEL"),
        listOf("D", "E", "F", "(", "/"),
        listOf("7", "8", "9", ")", "*"),
        listOf("4", "5", "6", "AND", "-"),
        listOf("1", "2", "3", "OR", "+"),
        listOf("0", "<<", ">>", "XOR", "NOT"),
        listOf("=", "=", "=", "=", "=")
    )

    rows.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { btn ->
                val weight = if (btn == "=") 5f else 1f
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.weight(weight).height(48.dp),
                    themeIndex = themeIndex,
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
fun CalculatorButton(symbol: String, modifier: Modifier, themeIndex: Int, onClick: () -> Unit) {
    val isRetroMode = themeIndex > 0
    val activeRetroTheme = if (isRetroMode) RetroThemes[themeIndex - 1] else null

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
                    isSpecial -> activeRetroTheme!!.special
                    isOperator -> activeRetroTheme!!.operator
                    else -> activeRetroTheme!!.primary
                }
                isSpecial -> MaterialTheme.colorScheme.onErrorContainer
                isOperator -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        ),
        border = if (isRetroMode) BorderStroke(1.dp, activeRetroTheme!!.primary.copy(alpha = 0.2f)) else null
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
    fun evaluate(expression: String, isDeg: Boolean = true, radix: Int = 10): Double {
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
            fun parseFactor(base: Double? = null): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                if (eat('~'.code)) return parseFactor().toLong().inv().toDouble()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch in '0'.code..'9'.code) || ch == '.'.code || (radix > 10 && ch in 'A'.code..'F'.code)) {
                    while ((ch in '0'.code..'9'.code) || ch == '.'.code || (radix > 10 && ch in 'A'.code..'F'.code)) nextChar()
                    val s = expr.substring(startPos, pos)
                    x = try {
                        if (radix != 10) s.toLong(radix).toDouble() else s.toDouble()
                    } catch (e: Exception) {
                        0.0
                    }
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

                if (radix == 10 && eat('^'.code)) x = x.pow(parseFactor())

                // Handle percentage (postfix %)
                while (eat('%'.code)) {
                    if (base != null) {
                        x = base * (x / 100.0)
                    } else {
                        x /= 100.0
                    }
                }

                return x
            }

            fun parseTerm(base: Double? = null): Double {
                var x = parseFactor(base)
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm(base = x)
                    else if (eat('-'.code)) x -= parseTerm(base = x)
                    else return x
                }
            }

            fun parseBitwise(): Double {
                var x = parseExpression()
                while (true) {
                    if (eatString("<<")) x = (x.toLong() shl parseExpression().toInt()).toDouble()
                    else if (eatString(">>")) x = (x.toLong() shr parseExpression().toInt()).toDouble()
                    else if (eat('&'.code)) x = (x.toLong() and parseExpression().toLong()).toDouble()
                    else if (eat('^'.code)) x = (x.toLong() xor parseExpression().toLong()).toDouble()
                    else if (eat('|'.code)) x = (x.toLong() or parseExpression().toLong()).toDouble()
                    else return x
                }
            }
        }

        nextChar()
        val result = if (radix != 10) parser.parseBitwise() else parser.parseExpression()
        if (pos < expr.length) throw RuntimeException("Unexpected: " + expr[pos])
        return result
    }
}