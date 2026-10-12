package com.example

import com.example.data.backup.CsvHelper
import com.example.data.backup.ExcelXmlHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCsvHelperEscape() {
        val normal = CsvHelper.escapeCsv("Santri Al-Qur'an")
        assertEquals("Santri Al-Qur'an", normal)

        val withComma = CsvHelper.escapeCsv("Lubuklinggau, Sumsel")
        assertEquals("\"Lubuklinggau, Sumsel\"", withComma)

        val withQuote = CsvHelper.escapeCsv("Juz 'Amma \"Surah\"")
        assertEquals("\"Juz 'Amma \"\"Surah\"\"\"", withQuote)
    }

    @Test
    fun testExcelWorkbookXmlGeneration() {
        val sheet1 = ExcelXmlHelper.SheetData(
            name = "Data Santri",
            headers = listOf("NIS", "Nama Lengkap", "Program"),
            rows = listOf(
                listOf("UA001", "Muhammad Al-Fatih", "Tahfidz Khusus"),
                listOf("UA002", "Abdullah Azzam", "Tahfidz Khusus")
            )
        )
        val xml = ExcelXmlHelper.generateWorkbook(listOf(sheet1))
        assertTrue(xml.contains("<?xml version=\"1.0\""))
        assertTrue(xml.contains("<Worksheet ss:Name=\"Data Santri\">"))
        assertTrue(xml.contains("Muhammad Al-Fatih"))
    }
}
