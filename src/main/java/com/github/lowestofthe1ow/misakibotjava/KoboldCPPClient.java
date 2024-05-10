package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.entities.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Timer;
import java.util.TimerTask;

public class KoboldCPPClient {
  /** A StringBuilder modified dynamically to create the context for prompts */
  public final StringBuilder context = new StringBuilder();
  /*
   * TODO: Implement KoboldCPP's WorldInfo feature
   * 
   * private final StringBuilder worldInfo = new StringBuilder();
   */

  /** The imported Memory field from resources/memory.txt */
  private final String memory;
  /** The imported Author's Note field from resources/authorsnote.txt */
  private final String authorsNote;

  /*
   * The OpenHermes 2.5 Mistral 7B model uses the ChatML format for its prompts.
   * https://learn.microsoft.com/en-us/azure/ai-services/openai/how-to/chat-markup-language
   */
  private class ChatML {
    /**
     * The role name injected next to the start tag. Usually "system", "user", or "assistant" sometimes paired with the
     * tag " name=" to track sender names
     */
    private final String role;
    /** The message to be placed in between tags */
    private final String message;

    /**
     * Builds the ChatML-formatted string to send to the AI model. Generally results in strings such as:
     * "<|im_start|>user name=lowestofthelow\\nHello!<|im_end|>\\n"
     */
    public String build() {
      return "<|im_start|>" + role + "\\n" + message + "<|im_end|>\\n";
    }

    ChatML(String role, String message) {
      this.role = role;
      this.message = message;
    }
  }

  /**
   * Sends a request to KoboldCPP's api/v1/generate/ based on the content of messageObject
   * 
   * @param messageObject The message that triggered the bot replying with a response
   * @throws Exception
   */
  public void requestGenerate(Message messageObject) throws Exception {
    /* Timer for repeatedly sending the "is typing..." status to Discord */
    final Timer timer = new Timer();
    /*
     * The raw content of the user's message. getContentDisplay() returns the content as if it were displayed on
     * Discord's UI, which makes it more readable
     */
    final String userRaw = messageObject.getContentDisplay()
        /* Replace all instances of "@MisakiBot" with "Misaki" to make the prompt clearer */
        .replaceAll("@MisakiBot", "Misaki");
    /* The user's prompt formatted in ChatML */
    final String userChatML = new ChatML("user name=" + messageObject.getMember().getUser().getName(), userRaw).build();

    /* Construct a request to KoboldCPP. Ends in an open "assistant" tag for the bot to complete */
    final KoboldCPPGenerateRequest request = new KoboldCPPGenerateRequest(
        /* [Memory][World info] */
        memory /* + worldInfo */,
        /* [Context][Author's note][User message]<|im_start|>assistant\\n */
        context.toString() + authorsNote + userChatML + "<|im_start|>assistant\\n");

    /* Send the "is typing..." status to Discord every 10 seconds */
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        messageObject.getChannel().sendTyping().queue();
      }
    }, 0, 10000);

    /* Send the request asynchronously and await a response */
    request.generate().thenAccept((response) -> {
      try {
        /* Extract the raw content of the bot's response */
        final String responseRaw = new ObjectMapper().readTree(response.body())
            /* Read JSON tree at path /results/0/text and get as text */
            .at("/results/0/text").asText()
            /* Remove closing tag from received response */
            .replace("<|im_end|>", "")
            /* Fix newlines */
            .replace("\\n", "\n");
        /*
         * Add the following to the chat context: [User message][Bot message]
         */
        context.append(userChatML + new ChatML("assistant", responseRaw).build());
        /* Reply to the message and stop the "is typing..." timer */
        messageObject.reply(responseRaw).queue((botMessageObject) -> {
          timer.cancel();
          timer.purge();
        });
      } catch (Exception e) {
        /* Handle the errors here, idk lmfao */
        messageObject.reply("Oh nooo, an error. Whatever will I do?\n```\n" + e.getMessage() + "\n```").queue();
      }
    });
  }

  KoboldCPPClient() throws IOException {
    InputStream authorsNoteStream = this.getClass().getResourceAsStream("/authorsnote.txt");
    InputStream memoryStream = this.getClass().getResourceAsStream("/memory.txt");

    authorsNote = new ChatML("system", new String(authorsNoteStream.readAllBytes(), StandardCharsets.UTF_8)).build();
    memory = new ChatML("system", new String(memoryStream.readAllBytes(), StandardCharsets.UTF_8)).build();
  }
}
