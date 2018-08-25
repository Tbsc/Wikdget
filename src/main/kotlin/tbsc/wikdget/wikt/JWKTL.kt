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

import de.tudarmstadt.ukp.jwktl.JWKTL
import de.tudarmstadt.ukp.jwktl.api.IPronunciation
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import org.jsoup.Jsoup
import tbsc.wikdget.Log
import tbsc.wikdget.WikdgetArgs
import tbsc.wikdget.wikt.nl.NLAdjTemplateHandler
import tbsc.wikdget.wikt.nl.NLNounTemplateHandler
import tbsc.wikdget.wikt.nl.NLVerbTemplateHandler
import tornadofx.*
import java.io.File
import java.net.URL
import kotlin.system.measureTimeMillis

/**
 * @author tbsc on 15/08/2018
 */
object Database {
    var wikt: IWiktionaryEdition by singleAssign()

    fun load() {
        Log.i("Loading database...")

        val time = measureTimeMillis {
            wikt = JWKTL.openEdition(File(WikdgetArgs.instance.databasePath))
        }
        Log.i("Database loaded in $time milliseconds")

        Runtime.getRuntime().addShutdownHook(Thread(Database::close))
    }

    private fun close() {
        Log.i("Closing database...")
        wikt.close()
        Log.i("Database closed")
    }

    /**
     * Searches through Wiktionary for [query] and returns a page if found, else null
     */
    fun findPage(query: String) = wikt.getPageForWord(query) ?: null

    /**
     * Filters the entries in [page] and returns only those in [lang].
     */
    fun findEntriesForLanguage(page: IWiktionaryPage, lang: Language) = page.entries.filter { it.wordLanguage == lang }

    /**
     * Shorter way of getting [ILanguage] instances
     */
    fun getLang(lang: String) = Language.get(lang)

    /**
     * Parses Wiktionary for the direct URL to [filename]. If not found, returns null.
     */
    fun getAudioURL(filename: String): String? {
        var audioUrl: String? = null
        val time = measureTimeMillis {
            // Format of the URL for media files
            val wikiPageUrl = "https://en.wiktionary.org/wiki/File:$filename"
            // Get the page from the internet and parse it
            val document = Jsoup.parse(URL(wikiPageUrl), 3000)
            // The audio player element has the .internal class, so we use this selector to find its element
            val elements = document.select(".internal")
            // The link to the audio file itself is in the href attribute in the audio player element
            val baseAudioUrl = elements.attr("href")
            // This is not a null safe procedure: If a link isn't found, it returns null so checks are needed!
            audioUrl = if (baseAudioUrl.isEmpty()) null else "https:$baseAudioUrl"
        }
        Log.v("Fetching URL for audio file $filename took ${time}ms")
        return audioUrl
    }

    /**
     * Converts the pronunciations of [entry] to audio file URLs and filters out if wanted with [pronPredicate].
     */
    fun getAudioUrlsForEntry(entry: IWiktionaryEntry, pronPredicate: (IPronunciation) -> Boolean = { true }) =
            entry.pronunciations
                    .filter(pronPredicate)
                    .map { getAudioURL(it.text) }
}

object Formatter {
    fun formatTemplates(wikiText: String) = wikiText
            .apply { TemplateParser.parse(this, NLNounTemplateHandler()) }
            .apply { TemplateParser.parse(this, NLVerbTemplateHandler()) }
            .apply { TemplateParser.parse(this, NLAdjTemplateHandler()) }
            // {{gloss}}: needs to be in parentheses [slaan]
            .apply { TemplateParser.parse(this, NumParamTemplateHandler("gloss", prefix = "(", suffix= ")")) }
            // {{non-gloss definition}}: needs to be put as-is, just without the template [die]
            .apply { TemplateParser.parse(this, NumParamTemplateHandler("non-gloss definition")) }
            // {{link}}: replace the link with just the link text, without the URL [motorrijwiel]
            .apply { TemplateParser.parse(this, NumParamTemplateHandler("link", "l", paramIndex = 1)) }
            // {{mention}}: [noemen]
            .apply { TemplateParser.parse(this, NumParamTemplateHandler("mention", "m", paramIndex = 1)) }
            // {{antonyms}}: [groot]
            .apply { TemplateParser.parse(this, ListTemplateHandler("antonyms", "ant", startIndex = 1, prefix = "Antonyms: ")) }
            // {{synonyms}}: [groot]
            .apply { TemplateParser.parse(this, ListTemplateHandler("synonyms", "syn", startIndex = 1, prefix = "Synonyms: ")) }
            // {{label}}: short explanation in parenthesis explaining the following definition [geloven]
            .apply { TemplateParser.parse(this, ListTemplateHandler("label", "lbl", "lb", startIndex = 1, prefix = "(", suffix = ")")) }
            // {{qualifier}}: explaining sort of the type of the definition [slaaf]
            .apply { TemplateParser.parse(this, ListTemplateHandler("qualifier", "qual", "q", "i", prefix = "(", suffix = ")")) }
            // [[thing]]: automatic Wiktionary links, removes brackets [verboden]
            .apply { replace("\\[\\[((?:[^|\\]]+?\\|)*)([^|\\]]+?)]]".toRegex(), "$2") }
            // removes italics/bold quotes
            .apply { replace("'''?".toRegex(), "") }
}

/**
 * Base class for handlers of template parsing.
 */
abstract class BaseTemplateHandler: TemplateParser.ITemplateHandler {

    override fun handle(template: TemplateParser.Template?) =
            if (template != null && templates.contains(template.name)) transform(template) else null

    /**
     * Transforms the [TemplateParser.Template] to a formatted nullable [String], for user viewing.
     */
    protected abstract fun transform(template: TemplateParser.Template): String?

    /**
     * List of templates this handler can support.
     */
    protected abstract val templates: List<String>

}