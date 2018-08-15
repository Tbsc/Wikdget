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
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tbsc.wikdget.Log
import tbsc.wikdget.beGone
import tbsc.wikdget.resetAndGone
import tbsc.wikdget.unGone
import tornadofx.*

/**
 * @author tbsc on 11/08/2018
 */
class SearchView: View("Wikdget: Search") {

    val langModel: LanguageModel by inject()

    val controller: SearchController by inject()

    var errorLabel: Label by singleAssign()

    override val root = vbox {
        Log.i("Showing SearchView")
        // A bit of spacing between controls
        spacing = 6.0
        padding = Insets(8.0)

        // Top bar, contains return button, search bar and search button
        hbox(4.0) {
            // Unicode for arrow symbol pointing to the left
            // Returns to language selection view
            button("\u2190") {
                // Prevents the button from shrinking and replacing the symbol with an ellipsis
                setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE)

                tooltip("Return to language selection")
                action {
                    Log.i("SearchView Back button clicked, returning to StartView")
                    replaceWith<StartView>()
                }
            }

            // Most user interaction will be here
            // Receives queries to search for
            textfield(controller.queryProperty) {
                promptText = "Enter a word..."
                hgrow = Priority.ALWAYS
                // Make it so when this view is loaded, the search text field is already highlighted
                // and the user doesn't need to click on the text field to start searching
                runLater {
                    requestFocus()
                }
            }

            button("Search") {
                // Main action, enter will cause it
                shortcut("Enter")
                action {
                    Log.v("Search button pressed")

                    // Make sure user actually entered a query
                    if (controller.query == "") {
                        Log.d("User tried to search without entering a query")
                        errorLabel.text = "No query entered"
                        errorLabel.unGone()
                        return@action
                    }

                    // Clear the error label so it doesn't stay up
                    errorLabel.resetAndGone()

                    // After making sure searching is possible,
                    // prevent clicking the search button
                    isDisable = true

                    runAsyncWithProgress {
                        controller.search()
                    } ui {
                        controller.results.setAll(it)
                        isDisable = false
                    }
                }
            }
        }

        listview(controller.results) {
            vgrow = Priority.ALWAYS
        }

        // Not always visible; used for displaying error messages
        errorLabel = label {
            beGone()
            textFill = Color.RED
            useMaxWidth = true
            alignment = Pos.CENTER
        }

        button("Pronunciation") {
            useMaxWidth = true
            shortcut("Ctrl+P")
            action {
                Log.v("Pronunciation button pressed")
                // Make it pronounce the word from the entry's audio clip
            }
        }
    }

}

class SearchController: Controller() {
    var queryProperty = SimpleStringProperty("")
    var query: String by queryProperty
    var results: ObservableList<String> = FXCollections.observableArrayList<String>()
    val langModel: LanguageModel by inject()

    /**
     * Searches the database for [queryProperty].
     * Returns a list of strings with the results for the query's entry
     */
    fun search(): List<String> {
        Log.v("Starting search for $query in ${langModel.item}")
        Thread.sleep(2000)
        return listOf("First list item", "Second list item", "Third list item")
    }
}

class LanguageModel: ItemViewModel<String>()