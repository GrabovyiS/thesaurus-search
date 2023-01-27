package dataTypes

/**
 * Класс для хранения пресетов запросов
 */
enum class QueryTypes {
    /**
     * Select-запрос
     */
    SELECT {
        private val type = "select [params] from"

        override fun toString(): String {
            return type
        }
    },

    /**
     * Insert-запрос
     */
    INSERT {
        private val type = "insert into"

        override fun toString(): String {
            return type
        }
    },
}
