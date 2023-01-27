package utils

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * класс для работы с файлом тезауруса
 *
 * @param path путь к файлу тезауруса
 */
class XmlSelector(private val path: String) {
    /**
     * функция извлечения значения из XML-файла
     *
     * @param attributeName название атрибута
     * @param attributeValue значение атрибута
     * @param attributeValueName название искомого атрипуба
     */
    fun getAttributeValuesByAttributeNameAndAttributeValue(
        attributeName: String,
        attributeValue: String,
        attributeValueName: String
    ): List<String> {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        val doc = readXml(path)
        val xpath = "/ItemSet/Item[@$attributeName = '$attributeValue']"

        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

        val itemList: MutableList<String> = ArrayList()
        for (i in 0 until itemsTypeT1.length) {
            val value = itemsTypeT1.item(i).attributes.getNamedItem(attributeValueName).nodeValue
            itemList.add(value)
        }

        return ArrayList(itemList)
    }

    fun getElementValuesByAttributeNameAndAttributeValue(attributeName: String, attributeValue: String): List<String> {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        val doc = readXml(path)
        val xpath = "/ItemSet/Item[contains(@$attributeName, '$attributeValue')]"
        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList
        val itemList: MutableList<String> = ArrayList()
        for (i in 0 until itemsTypeT1.length) {
            itemList.add(itemsTypeT1.item(i).textContent)
        }

        return ArrayList(itemList)
    }

    private fun readXml(path: String): Document {
        val xmlFile = File(path)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(xmlFile.readText()))
        val doc = dBuilder.parse(xmlInput)

        return doc
    }
}
