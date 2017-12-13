package nl.trbres.meetmanager.time

import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.util.StringConverter
import java.time.LocalTime

/**
 * @author Ruben Schellekens
 */
data class Time(var hours: Int, var minutes: Int, var seconds: Int, var hundreths: Int) : Comparable<Time> {

    companion object {

        val INVALID = Time(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
    }

    constructor(minutes: Int, seconds: Int, hundreths: Int) : this(0, minutes, seconds, hundreths)

    constructor(seconds: Int, hundreths: Int) : this(0, 0, seconds, hundreths)

    constructor() : this(0, 0, 0, 0) {
        val now = LocalTime.now()
        hours = now.hour
        minutes = now.minute
        seconds = now.second
        hundreths = (System.currentTimeMillis() % 1000 / 10).toInt()
    }

    /**
     * Converts the timestamp to hunderths of a second.
     */
    fun toHundreths() = (((hours * 60L) + minutes) * 60L + seconds) * 100L + hundreths

    /**
     * Checks whether the time equals zero.
     */
    @JsonIgnore
    fun isZero() = hours == 0 && minutes == 0 && seconds == 0 && hundreths == 0

    override fun compareTo(other: Time) = toHundreths().compareTo(other.toHundreths())

    override fun toString() = if (this == INVALID) {
        "INVALID"
    }
    else if (hours > 0) {
        String.format("%2d:%02d:%02d.%02d", hours, minutes, seconds, hundreths)
    }
    else if (minutes > 0) {
        String.format("%d:%02d.%02d", minutes, seconds, hundreths)
    }
    else {
        String.format("%2d.%02d", seconds, hundreths)
    }
}

/**
 * Converts strings to [Time] objects and vice versa.
 *
 * @author Ruben Schellekens
 */
open class TimeConverter : StringConverter<Time>() {

    override fun toString(time: Time?) = time?.toString() ?: ""

    override fun fromString(string: String?): Time {
        val time = string?.replace(Regex("[^\\d]"), "") ?: return Time(0, 0)
        return when (time.length) {
            1 -> Time(0, time(0..0))
            2 -> Time(0, time(0..1))
            3 -> Time(time(0..0), time(1..2))
            4 -> Time(time(0..1), time(2..3))
            5 -> Time(time(0..0), time(1..2), time(3..4))
            6 -> Time(time(0..1), time(2..3), time(4..5))
            7 -> Time(time(0..0), time(1..2), time(3..4), time(5..6))
            8 -> Time(time(0..1), time(2..3), time(4..5), time(6..7))
            else -> Time(0, 0)
        }
    }

    private operator fun String.invoke(range: IntRange) = slice(range).toInt()
}