package `in`.alfageeks.rangeslider.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.alfageeks.rangeslider.RangeSlider
import `in`.alfageeks.rangeslider.demo.ui.theme.RangeSliderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RangeSliderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var rangeStart by rememberSaveable { mutableIntStateOf(30) }
    var rangeEnd by rememberSaveable { mutableIntStateOf(80) }

    RangeSlider(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 40.dp),
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    ) { newRangeStart, newRangeEnd ->
        rangeStart = newRangeStart
        rangeEnd = newRangeEnd
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RangeSliderTheme {
        Greeting()
    }
}