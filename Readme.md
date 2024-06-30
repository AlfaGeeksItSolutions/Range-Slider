# RangeSlider

**RangeSlider** is a custom component for Jetpack Compose that enables users to select a range of values with customizable thumbs and track colors. It provides smooth, responsive interactions and real-time updates on value changes, making it ideal for applications requiring range selection, such as price filters and time selectors.

## Installation

Add the following dependency to your project's `build.gradle.kts` file:

```kotlin
implementation("in.alfageeks:range-slider:0.0.2")
```

## How to Use

### Example Usage:

```kotlin
// Define mutable state variables for range start and end
var rangeStart by remember { mutableIntStateOf(10) }
var rangeEnd by remember { mutableIntStateOf(50) }

// Example usage within a Composable function
Column(
    verticalArrangement = Arrangement.spacedBy(20.dp),
    modifier = Modifier.padding(vertical = 20.dp)
) {
    // Basic usage with default settings
    RangeSlider(
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    ) { newRangeStart, newRangeEnd ->
        rangeStart = newRangeStart
        rangeEnd = newRangeEnd
    }
    
    // Customized RangeSlider with modified track height and thumb size
    RangeSlider(
        rangeStart = rangeStart,
        rangeEnd = rangeEnd,
        trackHeight = 2.dp,
        thumbSize = 20.dp
    ) { newRangeStart, newRangeEnd ->
        rangeStart = newRangeStart
        rangeEnd = newRangeEnd
    }
}
```

## Parameters

- **min**: Minimum value for slider.
- **max**: Maximum value for slider.
- **rangeStart**: Initial start value of the range.
- **rangeEnd**: Initial end value of the range.
- **onChange**: Callback function triggered when the range values change.

## License
```
Copyright 2024 AlfaGeeks IT Solutions.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```