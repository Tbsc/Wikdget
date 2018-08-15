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

package tbsc.wikdget

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label

/**
 * Various utilities to make life a bit easier
 * @author tbsc on 11/08/2018
 */

/**
 * Short way to make [SimpleStringProperty] from [String]s.
 */
fun String.prop() = SimpleStringProperty(this)

/**
 * Makes the [Label] be "gone" (like in Android XML layouts) and makes it not take any space in the node.
 */
fun Label.beGone() {
    isManaged = false
}

/**
 * Makes the [Label] visible again and makes it take space in the node
 */
fun Label.unGone() {
    isManaged = true
}

fun Label.resetAndGone() {
    beGone()
    text = ""
}