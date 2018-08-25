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

package tbsc.wikdget.wikt.nl

import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import tbsc.wikdget.wikt.BaseTemplateHandler

/**
 * Transforms {{nl-noun form of}} templates to a short string explaining what the noun is a form of, and what
 * type of form it is.
 * @author tbsc on 20/08/2018
 */
class NLNounTemplateHandler : BaseTemplateHandler() {
    override val templates: List<String> = listOf("nl-noun form of")

    override fun transform(template: TemplateParser.Template): String? {
        val word = template.getNumberedParam(1)

        return when (template.getNumberedParam(0)) {
            "dim" -> "diminutive of $word"
            "pl" -> "plural form of $word"
            "acc" -> "(archaic) accusative form of $word"
            "gen" -> "(archaic) genitive form of $word"
            "dat" -> "(archaic) dative form of $word"
            else -> null
        }
    }
}

/**
 * Template handler for {{nl-verb form of}} templates that will replace them with the type
 * of formatted string you would see on the Wiktionary site, example:
 * {{nl-verb form of|p=2-gij|n=sg|t=pres|zijn}} -> second-person (gij) singular present indicative of zijn
 * There are tests for varying cases of this template.
 */
class NLVerbTemplateHandler : BaseTemplateHandler() {
    override val templates: List<String> = listOf("nl-verb form of")

    override fun transform(template: TemplateParser.Template): String? {
        val result = StringBuilder()

        // Person of the verb (1st, 2nd, 2nd-u, 2nd-gij, 3rd and any combination)
        // possible values are 1 to 3 discrete digits from 1 to 3 and optionally also a hyphen and gij or u
        if (template.getNamedParam("p") != null) {
            // The persons param is a string containing chars meaning what this verb form is of
            val personsParamSplit = template.getNamedParam("p").split("-")
            // The persons
            val persons = personsParamSplit[0].toCharArray()
            // gij or u or null if doesn't exist
            val gijOrU = if (personsParamSplit.size == 2) personsParamSplit[1] else null

            for (i in persons.indices) {
                val personChar = persons[i]
                result.append(when (personChar) {
                    '1' -> "first-"
                    '2' -> "second-"
                    '3' -> "third-"
                    else -> "impossible! "
                })
                if (i < persons.size - 1) {
                    result.append(" and ")
                }
            }

            result.append("person ")

            // Not always, so add it only if it is
            if (gijOrU != null) {
                result.append("($gijOrU) ")
            }
        }

        // Number of the verb (sg: singular, pl: plural)
        val nParam = template.getNamedParam("n")
        result.append(when (nParam) {
            "sg" -> "singular "
            "pl" -> "plural "
            else -> ""
        })


        // Tense of the verb (pres: present, past)
        val tParam = template.getNamedParam("t")
        result.append(when (tParam) {
            "pres" -> "present "
            "past" -> "past "
            else -> ""//"impossible! "
        })

        // Moods of the verb form are grouped by + signs in the m parameter
        // Possible values are subj: subjunctive, imp: imperative, ptc: participle, ind: indicative
        val mParam = template.getNamedParam("m")
        if (mParam != null) {
            val moods = mParam.split('+')

            for (i in moods.indices) {
                val mood = moods[i]

                result.append(when (mood) {
                    // Always archaic
                    "subj" -> "(archaic) subjunctive "
                    // Imperative is archaic only if it's plural, so check for that
                    "imp" -> {
                        if (nParam != null && nParam == "pl")
                            result.append("(archaic) ")
                        // Last statement so it's automatically a return statement
                        "imperative "
                    }
                    "ptc" -> "participle "
                    "ind" -> "indicative "
                    else -> "impossible! "
                })

                // Add "and " between moods if there is more than one and its not the last one now
                if (i < moods.size - 1) {
                    result.append("and ")
                }
            }
        } else {
            // If the mood parameter isn't specified the mood if indicative by default
            result.append("indicative ")
        }

        // Must exist based on template specs, so no need for null check
        // The infinitive this verb is a form of
        result.append("of ").append(template.getNumberedParam(0))

        // After everything, check for the sub param, signifying if it's a subordinate conjugation
        // If it exists in any way, add the subclause disclaimer
        if (template.getNamedParam("sub") != null) {
            result.append(" (when using a subclause)")
        }

        return result.toString()
    }
}

/**
 * Template handler for adjective inflections. Example:
 * {nl-adj form of|infl|groter|comp-of=groot}} -> Inflected form of groter, the comparative of groot
 */
class NLAdjTemplateHandler : BaseTemplateHandler() {
    override val templates: List<String> = listOf("nl-adj form of")

    override fun transform(template: TemplateParser.Template): String? {
        val word = template.getNumberedParam(1)

        var result = when (template.getNumberedParam(0)) {
            "infl" -> "inflected"
            "part" -> "partitive"
            "pred" -> "predicative"
            "comp" -> "comparative"
            "sup" -> "superlative"
            else -> "?"
        }

        result += " form of $word"

        val compOfParam = template.getNamedParam("comp-of")
        if (compOfParam != null) {
            result += ", the comparative of $compOfParam"
        }

        val supOfParam = template.getNamedParam("sup-of")
        if (supOfParam != null) {
            result += ", the superlative of $supOfParam"
        }

        return result
    }
}