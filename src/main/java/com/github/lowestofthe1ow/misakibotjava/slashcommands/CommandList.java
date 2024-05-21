package com.github.lowestofthe1ow.misakibotjava.slashcommands;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import java.util.HashMap;

import com.github.lowestofthe1ow.misakibotjava.util.RandomString;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandList {
  private final HashMap<String, CommandBody> commandMap;

  public HashMap<String, CommandBody> getCommandMap() {
    return commandMap;
  }

  public CommandBody getCommandBody(String key) {
    CommandBody commandBody = commandMap.get(key);

    if (commandBody == null)
      throw new IllegalStateException("The command " + key + " does not exist.");
    
    return commandBody;
  }

  public CommandList() {
    commandMap = new HashMap<String, CommandBody>();

    commandMap.put("say", new CommandBody.Builder()
        .setDescription("Make me say something!")
        .setCommandOptions(
            new OptionData(STRING, "content", "Your message.", true))
        .setCallback(event -> {
          String message = RandomString.randomize(
              "わかった！I'll go say that!",
              "はい～はい！I'll say that, then!",
              "言いましょう～");
          event.reply(message)
              .setEphemeral(true)
              .queue();
          event.getChannel()
              .sendMessage(event.getOption("content").getAsString())
              .queue();
        })
        .build());

    commandMap.put("makechoice", new CommandBody.Builder()
        .setDescription("Make me decide something for you, perfectly randomly!")
        .setCommandOptions(
            new OptionData(STRING, "choices", "A comma-separated list of items to choose from.", true))
        .setCallback(event -> {
          String choice = RandomString.randomize(
              event.getOption("choices")
                  .getAsString().trim()
                  .split("\\s*,\\s*"));
          event.reply("Hmm... I choose: **" + choice + "**!").queue();
        })
        .build());
  }
}
