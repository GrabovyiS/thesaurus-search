package utils

import interfaces.Normalizer
import java.util.*
import kotlin.collections.ArrayList

class NormalizerImpl(private val input: String) : Normalizer {

    var locInput = input

    override fun normalize(input: String): Boolean {
        try {
            val serviceWords = XmlSelector("./resources/thesaurus/service_words.xml").getElementValuesByAttributeNameAndAttributeValue("name", "service")
            locInput = locInput.replace("\\p{Punct}".toRegex(), "") // убрать пунктуацию
            // слова по отдельности без вспомогательных слов
            var words = locInput.lowercase().split(" ").filter { it -> !serviceWords.contains(it.uppercase()) }
            locInput = words.joinToString(" ") // слова обратно в строку
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun splitter(): ArrayList<String> {
        val words = locInput.split(" ")
        val doubleWordsList = ArrayList<String>()

        for (i in 0..words.size - 2) {
            doubleWordsList.add(words[i] + " " + words[i + 1])
        }

        return doubleWordsList
    }
}
