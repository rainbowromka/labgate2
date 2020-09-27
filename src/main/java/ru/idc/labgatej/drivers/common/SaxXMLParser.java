package ru.idc.labgatej.drivers.common;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.Enumeration;
import java.util.Hashtable;

public class SaxXMLParser extends DefaultHandler {
    private Hashtable tags;

    public void startDocument() throws SAXException {
        tags = new Hashtable();
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts)
            throws SAXException {

        Object value = tags.get(localName);

        if (value == null) {
            tags.put(localName, 1);
        }
        else {
            int count = (Integer) value;
            count++;
            tags.put(localName, count);
        }
    }

    public void endDocument() throws SAXException {
        Enumeration e = tags.keys();
        while (e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            int count = (Integer) tags.get(tag);
            System.out.println("Local Name \"" + tag + "\" occurs "
                    + count + " times");
        }
    }

}
