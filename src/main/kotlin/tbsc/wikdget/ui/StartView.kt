/*
 * This file is part of Wikdget.
 *
 * Wikdget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Wikdget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Wikdget.  If not, see <https://www.gnu.org/licenses/>.
 */

package tbsc.wikdget.ui

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tbsc.wikdget.Log
import tbsc.wikdget.beGone
import tbsc.wikdget.resetAndGone
import tbsc.wikdget.unGone
import tornadofx.*

/**
 * @author tbsc on 10/08/2018
 */
class StartView: View("Wikdget: Start") {

    val langController: LanguageController by inject()
    val langModel: LanguageModel by inject()
    val langSearchProperty = SimpleStringProperty("")

    override val root = vbox {
        Log.i("Showing StartView")
        // Make it look nice, spaced a bit
        spacing = 6.0
        padding = Insets(8.0)

        // Where users will search for their desired language
        // Has a hint, and when something is typed in, the languages ListView will filter out anything
        // that doesn't contain the query typed in here (ignoring case)
        // Pressing "enter" with this selected is equivalent to clicking on the "Confirm" button
        textfield(langSearchProperty) {
            promptText = "Search a language..."
            textProperty().addListener { _, _, new ->
                // "reset" the languages list and then keep only what matches the filter
                langController.languages.setAll(langController.allLanguages)
                langController.languages.retainAll(langController.languages
                        .filter { it.contains(new, true) }
                )
            }
        }

        // Shows available (and filtered) languages
        listview(langController.languages) {
            // Fill vertically (main control in the view)
            vgrow = Priority.ALWAYS
            // Selected language will be updated to langModel
            bindSelected(langModel)
            // Allows for quickly selecting a language, without needing to move the mouse to select
            // the only language left
            langController.languages.addListener(ListChangeListener {
                if (it.list.size == 1) {
                    Log.v("Only one language left in list (${it.list[0]}), selecting it")
                    selectionModel.select(0)
                }
            })
        }

        // Not always visible; used for displaying error messages
        val errorLabel = label {
            beGone()
            textFill = Color.RED
            useMaxWidth = true
            alignment = Pos.CENTER
        }

        button("Confirm") {
            // Fill horizontally
            useMaxWidth = true

            // Pressing "Enter" is equivalent to clicking on the button
            // Improves UX
            shortcut("Enter")

            // Checks to make a sure a language is selected and errors if not.
            // Otherwise, continues to the search view.
            action {
                Log.v("Making sure a language was selected")
                if (langModel.item == null) {
                    Log.v("No language was selected, showing error")
                    errorLabel.text = "Select a language"
                    errorLabel.unGone()
                } else {
                    Log.i("Selected language: ${langModel.item}")
                    // Remove the error label so it doesn't show if user returns from search view
                    errorLabel.resetAndGone()
                    replaceWith<SearchView>()
                }
            }
        }
    }
}

class LanguageController: Controller() {
    val allLanguages = FXCollections.observableArrayList("English", "Hebrew", "Dutch", "Italian",
            "French", "German", "Spanish", "Arabic")
    var languages = allLanguages.toList().observable()
}
