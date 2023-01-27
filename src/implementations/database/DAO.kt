package implementations.database

import dataTypes.QueryTypes
import interfaces.DAOConnection
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * класс взаимодействия с БД
 */
class DAO : DAOConnection {
    /**
     * подключение к БД
     */
    private var conn: Connection? = null
    override fun openConnection(dbName: String): Boolean {
        return try {
            conn = DriverManager.getConnection("jdbc:sqlite:$dbName")
            true
        } catch (e: Exception) {
            println("Ошибка подключения к БД: ${e.message}")
            false
        }
    }

    override fun closeConnection(): Boolean {
        return try {
            conn?.close()
            true
        } catch (e: Exception) {
            println("Ошибка отключения от БД: ${e.message}")
            false
        }
    }

    /** Вместо использования обязательного параметра column перепишем queryExecute -> executeQuery */

    fun executeQuery(query: String, type: QueryTypes): MutableList<Map<String, *>>? {
        if (type == QueryTypes.SELECT) {
            val response = conn?.createStatement()?.executeQuery(query)
            if (response != null) {
                // перевод resultset в mutablelist
                val metadata: ResultSetMetaData = response.metaData
                val columns = metadata.columnCount
                val results: MutableList<Map<String, *>> = ArrayList()
                while (response.next()) {
                    // проходим по строкам
                    val row: MutableMap<String, Any> = HashMap()
                    for (i in 1..columns) {
                        // проходим по колонкам
                        row[metadata.getColumnLabel(i).uppercase(Locale.getDefault())] = response.getObject(i)
                    }
                    results.add(row).toString()
                }
                return results
            }
        }
        if (type == QueryTypes.INSERT) {
            conn?.createStatement()?.executeUpdate(query)
        }
        return null
    }

    override fun queryExecute(query: String, column: String, type: QueryTypes): List<String>? {
        return null
    }
}
