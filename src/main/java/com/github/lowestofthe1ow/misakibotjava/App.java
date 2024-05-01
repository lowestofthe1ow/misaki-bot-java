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

import java.util.ArrayList;
import java.util.List;

public class App extends ListenerAdapter {
  BotEvent ongoingBotEvent = null;
  final CommandList commandList = new CommandList(this);
  
  public static void main(String[] arguments) throws Exception {
    List<CommandData> commandDataList = new ArrayList<CommandData>();

    JDA api = JDABuilder.createDefault("MTIzNDA0NDkyNjU5Mzg1OTY0NA.GxB5vi.LLT2FVhEPb_ecmqYXtWrBoUFjFYP_5dsRaI-3s")
        .enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new App()).build().awaitReady();

    /* TODO: Consider refactoring such that we won't need to instantiate a new CommandList here */
    new CommandList().commandHash.forEach((commandName, command) -> commandDataList
        .add(Commands.slash(commandName, command.description).addOptions(command.commandOptions)));

    api.updateCommands().addCommands(commandDataList).queue();
  }
  
  /** 
   * @param event The MessageReceivedEvent detected by the listener
   */
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot())
      return;
    String content = event.getMessage().getContentRaw();

    if (content.contains("<@1234044926593859644>")) {
      event.getMessage()
          .reply(RandomString.randomize("はい！みさきです～", "Pong!", "はい！", "Thaaat's me!", "えへへ～", "にゃん！", "Present!"))
          .queue();
    }
  }
  
  /** 
   * @param event The SlashCommandInteractionEvent detected by the listener
   */
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    new CommandHandler(this).executeHandler(event);
  }
  
  /** 
   * @param event The ButtonInteractionEvent detected by the listener
   */
  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    String buttonID = event.getComponentId();

    /* TODO: Pass processing of ButtonInteractionEvents to a separate class */
    switch (buttonID) {
    case "event.rps.rock":
    case "event.rps.paper":
    case "event.rps.scissors":
      if (ongoingBotEvent != null)
        ((RPS) ongoingBotEvent).makeChoice(event);
      break;
    case "event.rps.yes":
      if (ongoingBotEvent != null)
        ((RPS) ongoingBotEvent).beginGame(event);
      break;
    case "event.rps.no":
      if (ongoingBotEvent != null) {
        if (ongoingBotEvent.participants.contains(event.getUser())) {
          event.deferEdit().queue();
          ongoingBotEvent.callingHandler.timeOut(event.getMessage(), "Oh, nevermind then...");
        }
      }
    }
  }
}
