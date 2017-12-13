package nl.trbres.meetmanager.export

import com.lowagie.text.*
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.time.Date
import nl.trbres.meetmanager.time.Time
import nl.trbres.meetmanager.util.*
import java.io.File


/**
 * @author Ruben Schellekens
 */
object EventResultPrinter {

    /**
     * Prints the results of the given event to a PDF.
     *
     * The user gets prompted with a dialog to save the pdf.
     */
    @JvmStatic
    fun printResults(event: Event, eventNumber: Int, owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(eventNumber, owner) ?: return
        DEFAULT_FONT = Fonts.robotoRegular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(64f, 64f, 40f, 40f)

            writer.pageEvent = ResultListFooter

            write {
                // Title
                paragraph(meet.name, alignment = Paragraph.ALIGN_CENTER)
                paragraph("${meet.location}, ${meet.date}", alignment = Paragraph.ALIGN_CENTER)
                separator()
                spacing(4f)

                // Header
                table(3) {
                    cell(Paragraph(DEFAULT_LEADING, "Programma $eventNumber", DEFAULT_FONT), Rectangle.NO_BORDER)
                    with(event) {
                        cell(Paragraph(DEFAULT_LEADING, "${ages.first()[category]}, $distance $stroke", DEFAULT_FONT), Rectangle.NO_BORDER) {
                            horizontalAlignment = Rectangle.ALIGN_CENTER
                        }
                    }
                    cell(Paragraph(DEFAULT_LEADING, event.ages.joinToString(", "), DEFAULT_FONT), Rectangle.NO_BORDER) {
                        horizontalAlignment = Rectangle.ALIGN_RIGHT
                    }
                }
                spacing(4f)
                separator(3f)
                spacing(4f)

                // Result table.
                val ranks = event.ranks()
                val names = event.names()
                val clubs = event.clubs()
                val results = event.results()

                table(4) {
                    widths(3, 21, 19, 10)

                    cell(newParagraph("rang", Fonts.robotoSmall), Element.ALIGN_RIGHT)
                    cell(newParagraph("naam", Fonts.robotoSmall))
                    cell(newParagraph("vereniging", Fonts.robotoSmall))
                    cell(newParagraph("eindtijd", Fonts.robotoSmall), Element.ALIGN_RIGHT)

                    for (i in 0 until names.size) {
                        cell(newParagraph(ranks[i]), Element.ALIGN_RIGHT)
                        cell(newParagraph(names[i]))
                        cell(newParagraph(clubs[i]))
                        cell(newParagraph(results[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                    }
                }
            }
        }
    }

    /**
     * Generates all rank numbers/statusses in order.
     */
    private fun Event.ranks() = swimResults().mapIndexed { i, result ->
        result.status?.type?.abbreviation ?: "${i + 1}."
    }

    /**
     * Generates all the names that should be put on the event list (in order).
     */
    private fun Event.names() = swimResults().map { it.swimmer.name }

    /**
     * Generates all club names in order.
     */
    private fun Event.clubs() = swimResults().map { it.swimmer.club?.name ?: "" }

    /**
     * Generates all results in order.
     */
    private fun Event.results() = swimResults().map {
        if (it.status != null) "" else it.result.toString()
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(number: Int, owner: Window? = null): File? {
        val result = FileChooser().apply {
            title = "Uitslag opslaan..."
            initialFileName = "ResultList_%02d.pdf".format(number)
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastExportDirectory].whenNonNull {
                initialDirectory = it.file()
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastExportDirectory] = result.parent
        return result
    }

    /**
     * @author Ruben Schellekens
     */
    object ResultListFooter : PdfPageEventHelper() {

        override fun onEndPage(writer: PdfWriter?, document: Document?) {
            document ?: return
            val cb = writer?.directContent ?: return
            val club = Phrase("TRB-RES", Fonts.robotoSmall)
            val now = Time()
            val date = Date()
            val timestamp = Phrase("$date, %02du%02d".format(now.hours, now.minutes), Fonts.robotoSmall)
            val separator = LineSeparator()
            separator.drawLine(cb,
                    document.leftMargin(),
                    document.right(),
                    document.bottom() + 8
            )
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    club,
                    document.leftMargin(),
                    document.bottom() - 4, 0f
            )
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    timestamp,
                    document.right(),
                    document.bottom() - 4, 0f
            )
        }
    }
}