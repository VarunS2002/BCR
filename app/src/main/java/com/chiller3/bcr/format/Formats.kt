package com.chiller3.bcr.format

import android.content.Context
import com.chiller3.bcr.Preferences

object Formats {
    val all: Array<Format> = arrayOf(OpusFormat, AacFormat, FlacFormat, WaveFormat)
    private val default: Format = all.first { it.supported }

    /** Find output format by name. */
    private fun getByName(name: String): Format? = all.find { it.name == name }

    /**
     * Get the saved format from the preferences or fall back to the default.
     *
     * The parameter, if set, is clamped to the format's allowed parameter range.
     */
    fun fromPreferences(context: Context): Pair<Format, UInt?> {
        val savedFormatName = Preferences.getFormatName(context)

        // Use the saved format if it is valid and supported on the current device. Otherwise, fall
        // back to the default.
        val format = savedFormatName
            ?.let { getByName(it) }
            ?.let { if (it.supported) { it } else { null } }
            ?: default

        // Convert the saved value to the nearest valid value (eg. in case bitrate range or step
        // size in changed in a future version)
        val param = Preferences.getFormatParam(context, format.name)?.let {
            format.paramInfo.toNearest(it)
        }

        return Pair(format, param)
    }
}