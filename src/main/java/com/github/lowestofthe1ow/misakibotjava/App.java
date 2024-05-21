package com.github.lowestofthe1ow.misakibotjava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.lowestofthe1ow.misakibotjava.botevents.BotEvent;
import com.github.lowestofthe1ow.misakibotjava.koboldcpp.KoboldCPPClient;
import com.github.lowestofthe1ow.misakibotjava.slashcommands.CommandHandler;
import com.github.lowestofthe1ow.misakibotjava.slashcommands.CommandList;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App extends ListenerAdapter {
  /** TODO: Refactor */
  public final List<BotEvent> ongoingBotEvents = new ArrayList<BotEvent>();
  public final KoboldCPPClient llmClient = new KoboldCPPClient();

  /** A CommandList that lists all valid commands */
  private final CommandList commandList = new CommandList();

  public static void main(String[] arguments) throws Exception {
    Dotenv dotenv = Dotenv.configure().load();
    JDABuilder.createDefault(dotenv.get("API_KEY")).enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(new App()).build();
  }

  /**
   * Called when the JDA instance is ready.
   * 
   * @param event
   */
  @Override
  public void onReady(ReadyEvent event) {
    /* Create a List of CommandData to add to the bot's slash command list on Discord */
    List<CommandData> commandDataList = new ArrayList<CommandData>();

    commandList.getCommandMap()
        .forEach((commandName, command) -> commandDataList
            .add(Commands.slash(commandName, command.getDescription())
                .addOptions(command.getCommandOptions())));

    /* Update the bot's command list all at once here */
    event.getJDA().updateCommands().addCommands(commandDataList).queue();
  }

  /**
   * Called whenever a message is sent in a channel the bot has access to.
   * 
   * @param event The MessageReceivedEvent detected by the listener
   */
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getMessage().getMentions().getUsers().contains(event.getJDA().getSelfUser())) {
      try {
        llmClient.requestGenerate(event.getMessage());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Called whenever a slash command is performed by a user.
   * 
   * @param event The SlashCommandInteractionEvent detected by the listener
   */
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    /* Pass command handling to a newly created command handler. */
    new CommandHandler(this).executeHandler(event, commandList.getCommandBody(event.getName()));
  }

  /**
   * Called whenever a button is pressed by a user.
   * 
   * @param event The ButtonInteractionEvent detected by the listener
   */
  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    /*
     * Search for an element in ongoingBotEvents whose listeningMessages list contains the Message object that the event
     * is tied to; set to null if not found
     */
    BotEvent botEvent = ongoingBotEvents.stream()
        .filter((result) -> result.listeningMessages.contains(event.getMessage())).findFirst().orElse(null);

    if (botEvent != null)
      /* Pass button handling to the involved event */
      botEvent.handleButton(event);
  }

  App() throws IOException {
  }
}
