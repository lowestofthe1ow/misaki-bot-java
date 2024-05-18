package com.github.lowestofthe1ow.misakibotjava.koboldcpp;

import net.dv8tion.jda.api.entities.Message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KoboldCPPClient {
  /** A StringBuilder modified dynamically to create the context for prompts */
  public final StringBuilder context = new StringBuilder();
  /** A StringBuilder modified dynamically to add world info to memory */
  public final StringBuilder worldInfo = new StringBuilder();

  /** The imported Memory field from resources/memory.txt */
  private final String memory;
  /** The imported Author's Note field from resources/authorsnote.txt */
  private final String authorsNote;

  /** The imported world info field from resources/worldinfo.json */
  public ConcurrentHashMap<List<String>, String> worldInfoMap;

  /**
   * Updates the world info field based on the content of message TODO: Optimize
   * 
   * @param message The string to scan for key matches
   */
  public void updateWorldInfo(String message) {
    worldInfoMap.keySet().stream()
        /* Filter the stream sourced from the keys list */
        .filter((keys) -> keys.stream()
            /* Include entry in filter if the message contains any of the keys */
            .anyMatch((key) -> message.toLowerCase().contains(key.toLowerCase())))
        /* Iterate over the matches */
        .forEach((List<String> match) -> {
          /* Update world info fields */
          worldInfo.append(worldInfoMap.get(match));
          System.out.println("Added world info to memory: " + worldInfoMap.get(match));
          worldInfoMap.remove(match);
        });
  }

  /**
   * Initializes the world info field
   */
  public void initializeWorldInfo() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      InputStream worldInfoStream = this.getClass().getResourceAsStream("/worldinfo.json");
      String worldInfoJSON = new String(worldInfoStream.readAllBytes(), StandardCharsets.UTF_8);
      ArrayNode worldInfoArray = (ArrayNode) mapper.readTree(worldInfoJSON);

      TypeReference<List<String>> listType = new TypeReference<List<String>>() {
      };

      worldInfoMap = new ConcurrentHashMap<List<String>, String>();

      worldInfoArray.forEach((entry) -> {
        try {
          worldInfoMap.put(
              /* Add keys list */
              mapper.readValue(entry.get("keys").toString(), listType),
              /* Add description string */
              entry.get("description").asText());
        } catch (Exception e) {
          /* Handle the errors here, idk lmfao */
          System.out.println(e.getMessage());
        }
      });
    } catch (Exception e) {
      /* Handle the errors here, idk lmfao */
      System.out.println(e.getMessage());
    }

    System.out.println(worldInfoMap);
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
        .replaceAll("@MisakiBot", "");
    /* The user's prompt formatted in ChatML */
    final String userChatML = new ChatML("user name=" + messageObject.getAuthor().getName(), userRaw).build();

    /* Update World Info */
    updateWorldInfo(userRaw);

    /* Construct a request to KoboldCPP. Ends in an open "assistant" tag for the bot to complete */
    final KoboldCPPGenRequest request = new KoboldCPPGenRequest(
        /* [Memory][World info] */
        memory + worldInfo.toString(),
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
    request.sendGenRequest().thenAccept((response) -> {
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
        updateWorldInfo(responseRaw);
        /* Reply to the message and stop the "is typing..." timer */
        messageObject.reply(responseRaw).queue((botMessageObject) -> {
          timer.cancel();
          timer.purge();
        });
      } catch (Exception e) {
        /* Handle the errors here, idk lmfao */
        messageObject.reply("Oh nooo, an error. Error happened here lol. Whatever will I do?\n```\n" + e + "\n```")
            .queue();
      }
    });
  }

  public KoboldCPPClient() throws IOException {
    InputStream authorsNoteStream = this.getClass().getResourceAsStream("/authorsnote.txt");
    InputStream memoryStream = this.getClass().getResourceAsStream("/memory.txt");

    initializeWorldInfo();
    authorsNote = new ChatML("system", new String(authorsNoteStream.readAllBytes(), StandardCharsets.UTF_8)).build();
    memory = new ChatML("system", new String(memoryStream.readAllBytes(), StandardCharsets.UTF_8)).build();
  }
}
