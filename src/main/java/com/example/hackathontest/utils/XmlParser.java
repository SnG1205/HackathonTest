package com.example.hackathontest.utils;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XmlParser {
    public static Document convertStringToXml(String xmlString) {

        SAXBuilder sax = new SAXBuilder();

        // https://rules.sonarsource.com/java/RSPEC-2755
        // prevent xxe
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        try {
            Document doc = sax.build(new StringReader(xmlString));
            return doc;

        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static org.w3c.dom.Document convertToDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
