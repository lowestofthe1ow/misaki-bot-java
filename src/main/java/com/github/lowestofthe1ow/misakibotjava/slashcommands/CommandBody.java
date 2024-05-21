package com.github.lowestofthe1ow.misakibotjava.slashcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandBody {
  private final String description;
  private final List<OptionData> commandOptions;
  private final Consumer<SlashCommandInteractionEvent> callback;

  /**
   * Creates a CommandData object based on a given name.
   * 
   * @param name The name of the command
   * @return a CommandData object that contains the command's name, description, and options
   */
  public CommandData asCommandData(String name) {
    return Commands.slash(name, description).addOptions(commandOptions);
  }

  /**
   * Gets the command description.
   * 
   * @return the command description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the possible options a user may specify when using a command.
   * 
   * @return a List of OptionData containing the command options
   */
  public List<OptionData> getCommandOptions() {
    return commandOptions;
  }

  /**
   * Executes the command's callback.
   * 
   * @param event The SlashCommandInteractionEvent associated with the command input
   */
  public void execute(SlashCommandInteractionEvent event) {
    callback.accept(event);
  }

  private CommandBody(Builder builder) {
    this.description = builder.description;
    this.commandOptions = builder.commandOptions;
    this.callback = builder.callback;
  }

  public static class Builder {
    private String description;
    private List<OptionData> commandOptions;
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
