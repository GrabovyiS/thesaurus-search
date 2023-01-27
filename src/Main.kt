import implementations.ThesaurusAnswerChooser
import interfaces.AnswerChooser

fun main() {
    val answerChooser: AnswerChooser = ThesaurusAnswerChooser()

    val question1 = "Как делать отмывание денег?"
    val question2 = "Где находить моральные ценности?"
    val question3 = "Как выглядит хатка бобра?"

    val questions = arrayListOf<String>(question1, question2, question3)

    for (question in questions) {
        println(question + " - " + answerChooser.getAnswer(question))
    }
}
