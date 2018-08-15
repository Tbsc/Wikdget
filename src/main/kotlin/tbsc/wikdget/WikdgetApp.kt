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

import tbsc.wikdget.ui.StartView
import tornadofx.*

/**
 * Entry point for the program.
 * Doesn't do much.
 * @author tbsc on 10/08/2018
 */
class WikdgetApp : App(StartView::class)

fun main(args: Array<String>) {
    Log.i("Starting Wikdget")
    launch<WikdgetApp>(args)
}