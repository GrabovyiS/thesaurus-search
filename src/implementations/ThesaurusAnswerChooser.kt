package implementations

import dataTypes.QueryTypes
import implementations.database.DAO
import interfaces.AnswerChooser
import interfaces.Normalizer
import utils.NormalizerImpl
import utils.QueryBuilder
import utils.XmlSelector

/**
 * Класс выбора ответа
 * Алгоритм выбора ответа:
 * 1. Нрмализация запроса (как пройти в библиотеку) -> (как пройти библиотеку)
 * 2. Разбиваем ввод пользователя на части по два слова (как пройти библиотеку) -> (как пройти, пройти библиотеку)
 * 3. Ищем в тезаурусе подходящие строки по тегу "name" и достаем из него строку по тегу "lemma"
 * 4. Удаляем повторяющиеся слова
 * 5. Если в базе данных есть строка с набором лемм, соответствующих получившемуся массиву - выводим ответ
 */
class ThesaurusAnswerChooser : AnswerChooser {

    private val thresholdValue: Double = 0.50

    private val defaultAnswer = "Сообщение найти не удалось, пожалуйста, повторите запрос"

    override fun getAnswer(question: String): String {
        val xmlSelector = XmlSelector("./resources/thesaurus/senses.xml")

        val normalizer: Normalizer = NormalizerImpl(question)
        normalizer.normalize(question)
        val pairs = normalizer.splitter()

        val dao = DAO()
        val conn = dao.openConnection("wordsdb.db")
        val allQnAQuery = QueryBuilder.Builder(QueryTypes.SELECT, "questions_and_answers").build().toString()
        val QnA = dao.executeQuery(allQnAQuery, QueryTypes.SELECT) // получить вопросы и ответы

        // лемматизация
        var lemmas = arrayListOf<String>() // собираем леммы в массив

        for (pair in pairs) {
            val lemmaPairsList = xmlSelector.getAttributeValuesByAttributeNameAndAttributeValue("name", pair.uppercase(), "lemma")
            // ищем по словам, если двусловие не нашлось
            if (lemmaPairsList.isEmpty()) {
                val pairAsList = pair.split(" ") // строка из массива pairs -> массив

                for (word in pairAsList) {
                    val lemmasList = xmlSelector.getAttributeValuesByAttributeNameAndAttributeValue("name", word.uppercase(), "lemma")
                    for (lemma in lemmasList) {
                        lemmas.add(lemma)
                    }
                }
            } else {
                // если двусловие нашлось
                for (lemma in lemmaPairsList) {
                    lemmas.add(lemma)
                }
            }
        }

        // уникальные слова
        var uniqueWords = arrayListOf<String>()

        for (lemma in lemmas) {
            val lemmaWords = lemma.split(" ")
            for (word in lemmaWords) {
                if (word.lowercase() !in uniqueWords) {
                    uniqueWords.add(word.lowercase())
                }
            }
        }

        // проверка слов, вывод ответа
        if (QnA != null) {
            for (i in QnA.indices) {
                // по вопросам
                val question = QnA[i]["QUESTION"] as String
                val questionWords = question.split(" ").toMutableList() // текущий вопрос разбитый по словам

                if (uniqueWords.size <= questionWords.size) {
                    var thresholdCount = 0.0
                    val increaseThresholdBy = 1 / questionWords.size.toDouble() // добавляем долю, занимамую словом

                    for (j in questionWords.indices) {
                        if (questionWords[j].lowercase() in uniqueWords) {
                            thresholdCount += increaseThresholdBy
                        }
                        if (thresholdCount >= thresholdValue) {
                            // если нашли совпадение
                            return QnA[i]["ANSWER"] as String
                        }
                    }
                }
            }
        }

        return defaultAnswer
    }
}
