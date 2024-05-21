package com.github.lowestofthe1ow.misakibotjava.slashcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandBody {
  /** The command's description */
  private final String description;
  /** List of OptionData for the command */
  private final List<OptionData> commandOptions;
  /** Consumer to execute when the command is used */
  private final Consumer<SlashCommandInteractionEvent> callback;

  public String getDescription() {
    return description;
  }

  public List<OptionData> getCommandOptions() {
    return commandOptions;
  }

  public void execute(SlashCommandInteractionEvent event) {
    callback.accept(event);
  }

  private CommandBody(Builder builder) {
    this.description = builder.description;
    this.commandOptions = builder.commandOptions;
    this.callback = builder.callback;
  }

  public static class Builder {
    /** The command's description */
    private String description;
    /** List of OptionData for the command */
    private List<OptionData> commandOptions;
    /** BiConsumer to execute when the command is used */
    private Consumer<SlashCommandInteractionEvent> callback;

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setCommandOptions(OptionData... commandOptions) {
      this.commandOptions = Arrays.asList(commandOptions);
      return this;
    }

    public Builder setCallback(Consumer<SlashCommandInteractionEvent> callback) {
      this.callback = callback;
      return this;
    }

    public CommandBody build() {
      return new CommandBody(this);
    }

    public Builder() {
      this.description = "";
      this.commandOptions = new ArrayList<OptionData>();
      this.callback = event -> {
      };
    }
  }
}
