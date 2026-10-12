package com.example.data.backup

object ExcelXmlHelper {

    data class SheetData(
        val name: String,
        val headers: List<String>,
        val rows: List<List<Any?>>
    )

    fun generateWorkbook(sheets: List<SheetData>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<?mso-application progid=\"Excel.Sheet\"?>\n")
        sb.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n")
        sb.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n")
        sb.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n")

        // Styles
        sb.append(" <Styles>\n")
        sb.append("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n")
        sb.append("   <Alignment ss:Vertical=\"Center\"/>\n")
        sb.append("   <Font ss:FontName=\"Arial\" ss:Size=\"10\" ss:Color=\"#333333\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"HeaderStyle\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\" ss:WrapText=\"1\"/>\n")
        sb.append("   <Borders>\n")
        sb.append("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n")
        sb.append("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n")
        sb.append("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n")
        sb.append("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n")
        sb.append("   </Borders>\n")
        sb.append("   <Font ss:FontName=\"Arial\" ss:Size=\"10\" ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>\n")
        sb.append("   <Interior ss:Color=\"#0D5C3A\" ss:Pattern=\"Solid\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"RowStyle\">\n")
        sb.append("   <Borders>\n")
        sb.append("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#CCCCCC\"/>\n")
        sb.append("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#CCCCCC\"/>\n")
        sb.append("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#CCCCCC\"/>\n")
        sb.append("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#CCCCCC\"/>\n")
        sb.append("   </Borders>\n")
        sb.append("   <Font ss:FontName=\"Arial\" ss:Size=\"9\"/>\n")
        sb.append("  </Style>\n")
        sb.append(" </Styles>\n")

        // Sheets
        for (sheet in sheets) {
            val safeName = sheet.name.replace("[/\\\\?*\\]\\[]".toRegex(), " ").take(31)
            sb.append(" <Worksheet ss:Name=\"").append(escapeXml(safeName)).append("\">\n")
            sb.append("  <Table>\n")

            // Header row
            sb.append("   <Row ss:Height=\"24\">\n")
            for (header in sheet.headers) {
                sb.append("    <Cell ss:StyleID=\"HeaderStyle\"><Data ss:Type=\"String\">")
                sb.append(escapeXml(header))
                sb.append("</Data></Cell>\n")
            }
            sb.append("   </Row>\n")

            // Data rows
            for (row in sheet.rows) {
                sb.append("   <Row ss:Height=\"20\">\n")
                for (cell in row) {
                    val value = cell?.toString() ?: ""
                    val isNumber = isNumeric(value)
                    val dataType = if (isNumber) "Number" else "String"
                    sb.append("    <Cell ss:StyleID=\"RowStyle\"><Data ss:Type=\"").append(dataType).append("\">")
                    sb.append(escapeXml(value))
                    sb.append("</Data></Cell>\n")
                }
                sb.append("   </Row>\n")
            }

            sb.append("  </Table>\n")
            sb.append(" </Worksheet>\n")
        }

        sb.append("</Workbook>\n")
        return sb.toString()
    }

    private fun escapeXml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun isNumeric(str: String): Boolean {
        if (str.isEmpty()) return false
        if (str.startsWith("0") && str.length > 1 && !str.startsWith("0.")) return false // keep NIS/Phone as string
        return str.toDoubleOrNull() != null
    }
}
