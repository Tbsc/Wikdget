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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author tbsc on 12/08/2018
 */
object Log {
    /**
     * Info level log messgae.
     * Always printed out.
     */
    fun i(text: String, newline: Boolean = true) = log("I", text)

    /**
     * Warning level log message
     * Always printed, and is something that won't prevent the program from continuing
     * running, but that the user should know of and maybe fix.
     */
    fun w(text: String, newline: Boolean = true) = log("W", text)

    /**
     * Error level log message
     * Always printed and means something happened that prevents further running of the program.
     */
    fun e(text: String, newline: Boolean = true) = log("E", text)

    /**
     * Something has happened that prevents the program from continuing any further.
     * Typically this should only be called once in a program's execution, but nothing will
     * prevent you from doing so.
     * The program should (try to) end immediately after calling this.
     */
    fun f(text: String, newline: Boolean = true) = log("F", text)

    /**
     * Verbose level log message
     * Printed only when enabled.
     */
    fun v(text: String, newline: Boolean = true) = if (WikdgetArgs.instance.verbose) log("V", text) else {}

    /**
     * Used for debugging the program.
     * Printed only when enabled.
     */
    fun d(text: String, newline: Boolean = true) = if (WikdgetArgs.instance.debug) log("D", text) else {}

    private val threadName: String
        get() = Thread.currentThread().name

    /**
     * Prints in the format of [date-da-te--ti:m:e] [log-level] message
     */
    fun log(tag: String, text: String, newline: Boolean = true) {
        print("[${LocalDateTime.now().format(DateTimeFormatter
                .ofPattern("uuuu-MM-dd-HH:mm:ss"))}] [$tag/$threadName] $text ${if (newline) "\n" else ""}")
    }
}