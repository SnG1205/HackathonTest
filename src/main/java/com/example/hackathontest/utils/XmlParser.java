package com.example.hackathontest.utils;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.StringReader;

public class XmlParser {
    public static Document convertStringToXml(String xmlString) {

        SAXBuilder sax = new SAXBuilder();

        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        try {
            Document doc = sax.build(new StringReader(xmlString));
            return doc;

        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
