package com.github.lowestofthe1ow.misakibotjava.slashcommands;

import com.github.lowestofthe1ow.misakibotjava.util.RandomString;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import com.github.lowestofthe1ow.misakibotjava.App;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.List;

public class CommandList {
  /** The main App object that created the handler */
  private App callingApp;

  /** A HashMap mapping command IDs to Command objects */
  public final HashMap<String, Command> commandHash = new HashMap<String, Command>();

  public class Command {
    /** The command's description */
    public String description;
    /** BiConsumer to execute when the command is used */
    public BiConsumer<SlashCommandInteractionEvent, CommandHandler> handlerCallback;
    /** List of OptionData for the command */
    public List<OptionData> commandOptions;

    Command(String description, OptionData[] commandOptions,
        BiConsumer<SlashCommandInteractionEvent, CommandHandler> handlerCallback) {
      this.description = description;
      this.handlerCallback = handlerCallback;
      this.commandOptions = Arrays.asList(commandOptions);
    }
  }

  public CommandList(App callingApp) {
    this();
    this.callingApp = callingApp;
  }

  public CommandList() {
    commandHash.put("say", new Command(
        /* Command description */
        "Make me say something and bait lowest into doing whatever you want!",
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

    commandHash.put("makechoice", new Command(
        /* Command description */
        "Make me decide something for you, perfectly randomly!",
        /* Command options */
        new OptionData[] {
            new OptionData(STRING, "choices", "A comma-separated list of items to choose from.", true)
        },
        /* Command callback */
        (event, callingHandler) -> {
          String choice = RandomString.randomize(event.getOption("choices").getAsString().trim().split("\\s*,\\s*"));
          event.reply("Hmm... I choose: **" + choice + "**!").queue();
        }));

    commandHash.put("forgettibeam", new Command(
        /* Command description */
        "Make me forget all your chat history with me! (Clears the bot's chat context)",
        /* Command options */
        new OptionData[] {},
        /* Command callback */
        (event, callingHandler) -> {
          callingApp.llmClient.context.setLength(0);
          callingApp.llmClient.worldInfo.setLength(0);
          callingApp.llmClient.initializeWorldInfo();
          event.reply("This poor udon-obsessed soul will now proceed to forget everything.").queue();
        }));
  }
}
