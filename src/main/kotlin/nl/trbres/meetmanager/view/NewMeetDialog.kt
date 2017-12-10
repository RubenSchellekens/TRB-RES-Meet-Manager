package nl.trbres.meetmanager.view

import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.time.toDate
import nl.trbres.meetmanager.util.*
import tornadofx.*

/**
 * Dialog that prompts the user to create and select a new meet.
 *
 * @author Ruben Schellekens
 */
class NewMeetDialog : Fragment() {

    @Suppress("UNCHECKED_CAST")
    private val meet = params["meet"] as Reference<Meet>

    private lateinit var txtName: TextField
    private lateinit var dateDate: DatePicker
    private lateinit var txtLanes: TextField

    override val root = borderpane {
        title = "Nieuwe zwemwedstrijd"
        prefWidth = 384.0

        center {
            form {
                fieldset("Wedstrijdinformatie") {
                    field("Wedstrijdnaam") {
                        txtName = textfield { useMaxWidth = true }
                    }
                    field("Datum") {
                        dateDate = datepicker()
                    }
                    field("Banen (n-m)") {
                        txtLanes = textfield { promptText = "vb. 2-5" }
                    }
                }

                buttonbar {
                    button("Aanmaken").styleClass("green").action(::createMeet)
                    button("Annuleren").styleClass("red").action(::cancel)
                }
            }
        }
    }

    /**
     * Validates the input.
     *
     * @return `null` when there are problems, `Unit` when all is fine.
     */
    private fun validate(): Oopsie {
        if (txtName.text.isNullOrBlank()) {
            warning("Er is geen wedstrijdnaam opgegeven!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (dateDate.value == null) {
            warning("Er is geen datum geselecteerd!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (!txtLanes.text.matches(Regex("\\d+-\\d+"))) {
            warning("De gebruikte banen heeft een verkeerd formaat!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        val numbers = txtLanes.text.split("-")
        val first = numbers[0].toInt()
        val second = numbers[1].toInt()
        if (first > second) {
            warning(
                    "Het tweede baannummer mag niet kleiner zijn dan het eerste nummer!",
                    owner = currentWindow,
                    title = "Foute invoer"
            )
            return OOPS
        }

        return FINE
    }

    /**
     * Creates the new meet and close the dialog.
     */
    private fun createMeet() {
        validate() ?: return

        val numbers = txtLanes.text.split("-")
        val laneRange = numbers[0].toInt()..numbers[1].toInt()
        meet.value = Meet(txtName.text, dateDate.value.toDate(), laneRange)

        currentStage?.close()
    }

    /**
     * Don't create a new meet and close the dialog.
     */
    private fun cancel() {
        currentStage?.close()
    }
}