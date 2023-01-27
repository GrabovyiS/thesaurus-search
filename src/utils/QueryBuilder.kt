package utils

import dataTypes.QueryTypes

/**
 * Билдер SQL запросов
 *
 * @param table название таблицы
 * @param data данные внутри запроса
 * @param where параметры для запроса SELECT
 * @param values параметры для запроса INSERT
 */
class QueryBuilder(
    private var table: String,
    private var data: String = "",
    private var where: String? = null,
    private var values: String? = null
) {

    private constructor(builder: Builder) : this(
        builder.table,
        builder.data,
        builder.where,
        builder.values
    )

    /**
     * класс для приватного конструктора, реализует паттерн билдер
     *
     * @property type тип запроса
     * @property table название таблицы
     */
    class Builder(private val type: QueryTypes, val table: String) {

        var data: String = "select * from"
            private set

        var where: String? = ""
            private set

        var values: String? = ""
            private set

        /**
         * функция добавления названий выбираемых столбцов, если их несколько
         *
         * @property columns названия столбцов
         */
        fun withSelect(columns: List<String>) = apply {
            data = "("
            columns.forEach {
                data += "$it, "
            }
            data += ")"
            data = data.replace(", )", ")")
            data = type.toString().replace("[params]", data)
        }

        /**
         * перегруженная функция добавления названия столбца, если оно одно
         *
         * @property column название столбца
         */
        fun withSelect(column: String) = apply {
            data = type.toString().replace("[params]", column)
        }

        /**
         * функция добавления названия столбца в INSERT
         *
         * @property column название столбца
         */
        fun withInsert(column: String) = apply {
            data = type.toString().replace("[params]", column)
        }

        /**
         * функция добавления параметров WHERE в запрос SELECT
         *
         * @param columns названия столбцов
         * @param value передаваемые значения
         */
        fun withWhere(columns: List<String>, value: List<String>) = apply {
            where = "where"

            for (i in columns.indices)
                where += " ${columns[i]} = '${value[i]}' AND"

            where += ";"
            where = where!!.replace(" AND;", "")
        }

        /**
         * функция добавления параметров VALUES для INSERT
         */
        fun withValues(value: List<String>) = apply {
            values = "values"

            for (i in value.indices)
                values += " ${value[i]},"

            values += ";"
            values = values!!.replace(",;", "")
        }

        fun build() = QueryBuilder(this)
    }

    /**
     * переписанная функция toString возвращает готовый запрос
     */
    override fun toString(): String {
        return if (values == null) {
            "$data $table $where"
        } else {
            "$data $table $values"
        }
    }
}
