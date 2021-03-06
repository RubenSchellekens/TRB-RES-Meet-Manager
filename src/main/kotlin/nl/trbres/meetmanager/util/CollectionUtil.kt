package nl.trbres.meetmanager.util

/**
 * Search a map for a key by value.
 */
fun <K, V> Map<K, V>.getKey(value: V): K? {
    if (!values.contains(value)) {
        return null
    }
    return entries.first { it.value == value }.key
}

/**
 * Makes a range of valid indices.
 */
fun <T> List<T>.indexRange() = 0 until size