package com.github.lowestofthe1ow.misakibotjava;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.InputStream;

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

  CommandList(App callingApp) {
    this();
    this.callingApp = callingApp;
  }

  CommandList() {
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

    commandHash.put("rps", new Command(
        /* Command description */
        "Challenge me or someone else to rock-paper-scissors!",
        /* Command options */
        new OptionData[] {
            new OptionData(USER, "opponent", "Your opponent!", true)
        },
        /* Command callback */
        (event, callingHandler) -> {
          /* User and option validation should be handled here */
          if (event.getOption("opponent").getAsUser().isBot())
            event.reply("You can't play against a bot, silly! Unless...").setEphemeral(true).queue();
          else if (event.getOption("opponent").getAsUser().equals(event.getUser()))
            event.reply("Challenging yourself is a good thing and all, but not this time!").setEphemeral(true).queue();
          else {
            /* Create a new BotEvent, add it to the List of ongoing events, then do as needed here */
            RPS RPSEvent = new RPS(callingHandler, event.getUser(), event.getOption("opponent").getAsUser());
            callingApp.ongoingBotEvents.add(RPSEvent);
            RPSEvent.challengePrompt(event);
          }
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

    commandHash.put("jpdef", new Command(
        /* Command description */
        "Make me look up a word on the JMDict Japanese dictionary!",
        /* Command options */
        new OptionData[] {
            new OptionData(STRING, "word", "The word to seach for.", true)
        },
        /* Command callback */
        (event, callingHandler) -> {
          /* Acknowledge event with a deferred reply */
          event.deferReply().queue();

          /* Attempt to load JMDict XML file */
          try {
            InputStream jmdict_stream = this.getClass().getResourceAsStream("/JMdict_e_examp.xml");
            new JMDictXMLStAXReader(event, jmdict_stream).parse();
            /* Throw an error if file could not be loaded */
          } catch (Exception e) {
            event.getHook().sendMessage("Oh nooo, an error. Whatever will I do?\n```\n" + e.getMessage() + "\n```")
                .queue();
            /* Funny error message for when the EntityExpansionLimit property isn't set properly (see above TODO) */
            if (e.getMessage().contains("JAXP00010001"))
              event.getHook().sendMessage(
                  "(Psst. That means you should prooobably toss in a `-DentityExpansionLimit=0` when running the `.jar`)")
                  .queue();
          }
        }));
  }
}
