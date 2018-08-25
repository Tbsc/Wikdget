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

package tbsc.wikdget.test

import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import tbsc.wikdget.wikt.nl.NLNounTemplateHandler
import tbsc.wikdget.wikt.nl.NLVerbTemplateHandler

fun TemplateParser.Template.addParam(param: Pair<String?, String?>) {
    addParam(param.first, param.second)
}

/**
 * @author tbsc on 20/08/2018
 */
class NLVerbTemplateHandlerTest: StringSpec({

    /**
     * Short util method for quickly setting up template handlers
     */
    fun templateHandler(block: TemplateParser.Template.() -> Unit) = NLVerbTemplateHandler()
            .handle(TemplateParser.Template("nl-verb form of").apply(block))

    "gaat: nl-verb form of|p=23|n=sg|t=pres|gaan should be correctly formatted string" {
        templateHandler {
            addParam("p" to "23")
            addParam("n" to "sg")
            addParam("t" to "pres")
            // addParam("m", "subj")
            addParam("gaan")
        } shouldBe "second- and third-person singular present indicative of gaan"
    }

    "wijzigde: nl-verb form of|n=sg|t=past|m=ind+subj|wijzigen should be correctly formatted string" {
        templateHandler {
            // addParam("p" to "23")
            addParam("n" to "sg")
            addParam("t" to "past")
            addParam("m" to "ind+subj")
            addParam("wijzigen")
        } shouldBe "singular past indicative and (archaic) subjunctive of wijzigen"
    }

    "voorkomt: nl-verb form of|p=23|n=sg|t=pres|m=ind|voorkomen|sub=1|nodot=1 should be correctly formatted string" {
        templateHandler {
            addParam("p" to "23")
            addParam("n" to "sg")
            addParam("t" to "pres")
            addParam("m" to "ind")
            addParam("voorkomen")
            addParam("sub" to "1")
            addParam("nodot" to "1")
        } shouldBe "second- and third-person singular present indicative of voorkomen (when using a subclause)"
    }

    "aankondigend: nl-verb form of|t=pres|m=ptc|aankondigen should be correctly formatted string" {
        templateHandler {
            addParam("t" to "pres")
            addParam("m" to "ptc")
            addParam("aankondigen")
        } shouldBe "present participle of aankondigen"
    }

    "zijt: nl-verb form of|p=2-gij|n=sg|t=pres|zijn should be correctly formatted string" {
        templateHandler {
            addParam("p" to "2-gij")
            addParam("n" to "sg")
            addParam("t" to "pres")
            addParam("zijn")
        } shouldBe "second-person (gij) singular present indicative of zijn"
    }
})

class NLNounTemplateHandlerTest: StringSpec({
    /**
     * Short util method for quickly setting up template handlers
     */
    fun templateHandler(block: TemplateParser.Template.() -> Unit) = NLNounTemplateHandler()
            .handle(TemplateParser.Template("nl-noun form of").apply(block))

    "nl-noun form of|pl|kind should be correctly formatted string" {
        templateHandler {
            addParam("pl")
            addParam("kind")
        } shouldBe "plural form of kind"
    }
})