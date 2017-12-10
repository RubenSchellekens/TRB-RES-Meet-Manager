package nl.trbres.meetmanager.view

import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.time.toDate
import nl.trbres.meetmanager.util.onUnfocus
import nl.trbres.meetmanager.util.toIntRange
import nl.trbres.meetmanager.util.validate
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class General(val main: MainView) : BorderPane() {

    lateinit var txtName: TextField
    lateinit var dateDate: DatePicker
    lateinit var txtLanes: TextField

    init {
        center {
            form {
                fieldset("Wedstrijdinformatie") {
                    field("Wedstrijdnaam") {
                        txtName = textfield().validate(
                                { !text.isNullOrBlank() },
                                "Text mag niet leeg zijn",
                                { State.meet?.name = text; main.updateTitle() },
                                { text = State.meet?.name ?: "" }
                        )
                    }
                    field("Datum") {
                        dateDate = datepicker().onUnfocus {
                            State.meet?.date = value.toDate()
                        }
                    }
                    field("Banen in gebruik (\"x-y\")") {
                        txtLanes = textfield().validate(
                                {
                                    if (!text.matches(Regex("\\d+-\\d+"))) {
                                        return@validate false
                                    }
                                    val numbers = text.split("-")
                                    return@validate numbers[0].toInt() <= numbers[1].toInt()
                                },
                                "De gebruikte banen hebben een verkeerd formaat.\nVerwacht: 'n-m' waar n & m baannummers zijn (n <= m).\nVoorbeeld: 1-8 voor banen 1 t/m 8.",
                                { State.meet?.lanes = text.toIntRange() },
                                { val range = State.meet?.lanes ?: return@validate; text = "${range.start}-${range.endInclusive}" }
                        )
                    }
                }
            }
        }

        populate()
    }

    fun populate() {
        val meet = State.meet ?: return
        txtName.text = meet.name
        dateDate.value = meet.date.toLocalDate()
        txtLanes.text = "${meet.lanes.first}-${meet.lanes.endInclusive}"
    }
}