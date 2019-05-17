package nl.trbres.meetmanager.model

/**
 * @author Hannah Schellekens
 */
enum class Stroke(val strokeName: String) {

    BUTTERFLY("vlinderslag"),
    BACKSTROKE("rugslag"),
    BREASTSTROKE("schoolslag"),
    FREESTYLE("vrije slag"),
    MEDLEY("wisselslag");

    override fun toString() = strokeName
}