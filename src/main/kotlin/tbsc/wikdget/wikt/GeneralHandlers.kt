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

package tbsc.wikdget.wikt

import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser

/**
 * Replaces any template in [templates] with the numbered parameter in index [paramIndex], and also can surround
 * it with [prefix] and [suffix]. Used for templates such as {{gloss}}.
 * @author tbsc on 24/08/2018
 */
class NumParamTemplateHandler(vararg templates: String,
                              val paramIndex: Int = 0,
                              val prefix: String = "", val suffix: String = "")
    : BaseTemplateHandler() {
    override val templates = templates.toList()
    override fun transform(template: TemplateParser.Template): String? {
        val param = template.getNumberedParam(paramIndex)
        return if (param == null) null else prefix + param + suffix
    }
}

/**
 * Template handler for printing all numbered parameters of [templates] starting from [startIndex] comma separated,
 * printing [prefix] before and [suffix] after.
 */
class ListTemplateHandler(vararg templates: String,
                          val startIndex: Int = 0,
                          val prefix: String = "", val suffix: String = "")
    : BaseTemplateHandler() {
    override val templates = templates.toList()
    override fun transform(template: TemplateParser.Template): String? {
        val result = StringBuilder()

        for (i in startIndex..template.numberedParamsCount) {
            result.append(template.getNumberedParam(i))

            // Add a comma if there is another parameter after this one
            if (i < template.numberedParamsCount - 1) {
                result.append(", ")
            }
        }

        // Add the prefix and suffix after making sure there will be something between them
        val strResult = result.toString()
        return if (strResult == "") null else prefix + strResult + suffix
    }
}