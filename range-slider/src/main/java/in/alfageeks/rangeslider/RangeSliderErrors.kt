package `in`.alfageeks.rangeslider

internal val invalidMinMaxError = Pair("INVALID_MIN_MAX", "min must be less than max")
internal val invalidRangeStartError = Pair("INVALID_RANGE_START", "rangeStart >= min && rangeStart <= max")
internal val invalidRangeEndError = Pair("INVALID_RANGE_END", "rangeEnd >= min && rangeEnd <= max")
internal val invalidRangeStartEndError = Pair("INVALID_RANGE_START_END", "rangeStart must be less than rangeEnd value")
internal val invalidTrackHeightError = Pair("INVALID_TRACK_HEIGHT", "trackHeight must be less than or equal to thumbSize")
