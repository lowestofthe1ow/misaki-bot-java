package com.github.lowestofthe1ow.misakibotjava;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import java.util.Arrays;
import java.util.List;

/* TODO: Find another way around the JDK-enforced EntityExpansionLimit */
abstract class XMLStAXReader {
  /* The XMLInputFactory object that creates the XMLStreamReader for the file */
  private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
  /* The XMLStreamReader object for the file */
  public XMLStreamReader xmlStreamReader;

  abstract void parse() throws XMLStreamException;

  /** 
   * Searches for the first instance of a tag that is in a given list of tags
   * @param tags A list of the tags that trigger a return when encountered
   * @return int The index of the encountered tag in the given tag list (-1 if not found)
   * @throws XMLStreamException
   */
  public int searchForTag(String... tags) throws XMLStreamException {
    List<String> tagsList = Arrays.asList(tags);

    Boolean found = false;
    int eventType;
    
    while (xmlStreamReader.hasNext() && !found) {
      eventType = xmlStreamReader.next();
      /* Only check for START_ELEMENTs */
      if (eventType == XMLStreamReader.START_ELEMENT && tagsList.contains(xmlStreamReader.getLocalName()))
        found = true;
    }

    if (!found)
      /* Return -1 if not found */
      return -1;
    return tagsList.indexOf(xmlStreamReader.getLocalName());
  }

  XMLStAXReader(InputStream xmlStream) throws XMLStreamException {
    this.xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlStream);
  }
}
