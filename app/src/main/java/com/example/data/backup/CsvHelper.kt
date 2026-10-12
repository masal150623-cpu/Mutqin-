package com.example.data.backup

object CsvHelper {
    fun escapeCsv(value: Any?): String {
        if (value == null) return ""
        val stringValue = value.toString()
        val containsSpecial = stringValue.contains(",") ||
                stringValue.contains("\"") ||
                stringValue.contains("\n") ||
                stringValue.contains("\r")
        return if (containsSpecial) {
            "\"" + stringValue.replace("\"", "\"\"") + "\""
        } else {
            stringValue
        }
    }

    fun buildCsv(headers: List<String>, rows: List<List<Any?>>): String {
        val sb = StringBuilder()
        sb.append(headers.joinToString(separator = ",") { escapeCsv(it) })
        sb.append("\r\n")
        for (row in rows) {
            sb.append(row.joinToString(separator = ",") { escapeCsv(it) })
            sb.append("\r\n")
        }
        return sb.toString()
    }
}
