package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import net.dv8tion.jda.api.requests.GatewayIntent;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class App extends ListenerAdapter {
  /** A List that tracks ongoing BotEvents */
  public final List<BotEvent> ongoingBotEvents = new ArrayList<BotEvent>();
  /** A CommandList that lists all valid commands */
  public final CommandList commandList = new CommandList(this);

  public static void main(String[] arguments) throws Exception {
    /*
     * Create a List of CommandData to add to the bot's slash command list on Discord. A list of CommandData objects is
     * needed here because funny stuff happens if we try to add commands one by one (e.g. by iterating through a
     * CommandList)
     */
    List<CommandData> commandDataList = new ArrayList<CommandData>();

    Dotenv dotenv = Dotenv.configure().load();
    JDA api = JDABuilder.createDefault(dotenv.get("API_KEY"))
        .enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new App()).build().awaitReady();

    /* TODO: Consider refactoring such that we won't need to instantiate a new CommandList here */
    new CommandList().commandHash.forEach((commandName, command) -> commandDataList
        .add(Commands.slash(commandName, command.description).addOptions(command.commandOptions)));

    /* Update the bot's command list all at once here */
    api.updateCommands().addCommands(commandDataList).queue();
  }

  /**
   * Called whenever a message is sent in a channel the bot has access to.
   * @param event The MessageReceivedEvent detected by the listener
   */
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    String content = event.getMessage().getContentRaw();

    if (content.contains("<@1234044926593859644>")) {
      /* TODO: LLM implementation here */
      event.getMessage()
          //.reply(RandomString.randomize("はい！鳶沢みさきです～", "Pong!", "はい！", "Thaaat's me!", "えへへ～", "にゃ
          //ん！", "Present!"))
          .reply(String.valueOf(ongoingBotEvents))
          .queue();
    }
  }

  /**
   * Called whenever a slash command is performed by a user.
   * @param event The SlashCommandInteractionEvent detected by the listener
   */
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    /* Pass command handling to a newly created command handler. */
    new CommandHandler(this).executeHandler(event);
  }

  /**
   * Called whenever a button is pressed by a user.
   * @param event The ButtonInteractionEvent detected by the listener
   */
  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    /*
     * Search for an element in ongoingBotEvents whose listeningMessages list contains the Message object that the event
     * is tied to; set to null if not found
     */
    BotEvent botEvent = ongoingBotEvents.stream()
        .filter((result) -> result.listeningMessages.contains(event.getMessage()))
        .findFirst().orElse(null);

    if (botEvent != null)
      /* Pass button handling to the involved event */
      botEvent.handleButton(event);
  }
}
