package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

public class JMDictXMLStAXReader extends XMLStAXReader {
  /** The SlashCommandInteractionEvent detected by the listener */
  private SlashCommandInteractionEvent event;
  /** The StringBuilder object for storing the message to be sent in response */
  private StringBuilder message = new StringBuilder();
  
  /** 
   * Parses the JMDict XML file streamed to xmlStreamReader
   * @throws XMLStreamException
   */
  @Override
  public void parse() throws XMLStreamException {
    /* Check if there are any other "reb" or "keb" tags unread in the stream */
    while (searchForTag("reb", "keb") != -1)
      if (xmlStreamReader.getElementText().equals(event.getOption("word").getAsString())) {
        /* Check if tag encountered is "sense" but stop if it detects any of the others */
        while (searchForTag("gloss", "sense", "entry", "ent_seq") == 1) {
          /* Check if tag encountered is "gloss" but stop if it detects any of the others */
          while (searchForTag("gloss", "sense", "entry", "ent_seq") == 0)
            message.append(xmlStreamReader.getElementText() + "; ");
          message.append("\n");
        }
        message.append("\n");
      }

    if (!message.toString().isBlank())
      event.getHook().sendMessage("\'Kaaay, found these definitions for " + event.getOption("word").getAsString()
          + ":\n```\n" + message.toString() + "```").queue();
    else
      event.getHook().sendMessage("Eh? You're just making up words, right?").queue();
  }

  JMDictXMLStAXReader(SlashCommandInteractionEvent event, InputStream xmlStream) throws XMLStreamException {
    super(xmlStream);
    this.event = event;
  }
}
