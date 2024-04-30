package com.github.lowestofthe1ow.misakibotjava;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.List;

public class CommandList {
  App callingApp;

  public class Command {
    String description;
    BiConsumer<SlashCommandInteractionEvent, CommandHandler> handlerCallback;
    List<OptionData> commandOptions;
    Boolean isEvent;

    Command(String description, Boolean isEvent, OptionData[] commandOptions,
        BiConsumer<SlashCommandInteractionEvent, CommandHandler> handlerCallback) {
      this.description = description;
      this.isEvent = isEvent;
      this.handlerCallback = handlerCallback;
      this.commandOptions = Arrays.asList(commandOptions);
    }
  }

  final HashMap<String, Command> commandHash = new HashMap<String, Command>();

  CommandList(App callingApp) {
    this();
    this.callingApp = callingApp;
  }

  CommandList() {
    commandHash.put("say", new Command(
        /* Command description */
        "Make me say something and bait lowest into doing whatever you want!",
        /* Whether command involves a BotEvent */
        false,
        /* Command options */
        new OptionData[] {
            new OptionData(STRING, "content", "Your message", true)
        },
        /* Command callback */
        (event, callingHandler) -> {
          event.reply(RandomString.randomize("わかった！I'll go say that!", "はい～はい！I'll say that, then!", "言いましょう～"))
              .setEphemeral(true).queue();
          event.getChannel().sendMessage(event.getOption("content").getAsString()).queue();
        }));

    commandHash.put("rps", new Command(
        /* Command description */
        "Challenge me or someone else to rock-paper-scissors!",
        /* Whether command involves a BotEvent */
        true,
        /* Command options */
        new OptionData[] {
            new OptionData(USER, "opponent", "Your opponent!", true)
        },
        /* Command callback */
        (event, callingHandler) -> {
          if (event.getOption("opponent").getAsUser().isBot())
            event.reply("You can't play against a bot, silly! Unless...").setEphemeral(true).queue();
          else if (event.getOption("opponent").getAsUser().equals(event.getUser()))
            event.reply("Challenging yourself is a good thing and all, but not this time!").setEphemeral(true).queue();
          else {
            callingApp.ongoingBotEvent = new RPS(callingHandler, event.getUser(),
                event.getOption("opponent").getAsUser());
            ((RPS) (callingApp.ongoingBotEvent)).challengePrompt(event);
          }
        }));
  }
}
