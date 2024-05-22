package com.github.lowestofthe1ow.misakibotjava.slashcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.lowestofthe1ow.misakibotjava.botevents.BotEvent;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandBody {
  private final String description;
  private final List<OptionData> commandOptions;
  private final Consumer<SlashCommandInteractionEvent> callback;
  private final Supplier<BotEvent> botEventSupplier;

  /**
   * Creates a {@link CommandData} object based on a given name.
   * 
   * @param name The name of the command
   * @return a {@link CommandData} object that contains the command's name, description, and options
   */
  public CommandData asCommandData(String name) {
    return Commands.slash(name, description).addOptions(commandOptions);
  }

  /**
   * {@return the command description}
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns a List of the possible options a user may specify when using a command.
   * 
   * @return a List of {@link OptionData} containing the command options
   */
  public List<OptionData> getCommandOptions() {
    return commandOptions;
  }

  /**
   * Executes the command's callback.
   * 
   * @param event The {@link SlashCommandInteractionEvent} associated with the command input
   */
  public void executeCallback(SlashCommandInteractionEvent event) {
    callback.accept(event);
  }

  /**
   * Checks if the command can supply a {@link BotEvent} object
   * 
   * @return true if a {@link BotEvent} {@link Supplier} is tied to the object, false otherwise
   * @see {@link Builder#setBotEventSupplier}
   */
  public Boolean canCreateBotEvent() {
    return botEventSupplier != null;
  }

  /**
   * Creates a {@link BotEvent} through the {@link #botEventSupplier} object.
   * 
   * @return the created {@link BotEvent} instance
   * @throws NullPointerException if no {@link BotEvent} {@link Supplier} is tied to the object
   * @see {@link #canCreateBotEvent} to first check if the {@link Supplier} object exists
   * @see {@link Builder#setBotEventSupplier}
   */
  public BotEvent createBotEvent() {
    if (botEventSupplier == null)
      throw new NullPointerException("No BotEvent Supplier object is tied to this CommandBody");

    return botEventSupplier.get();
  }

  private CommandBody(Builder builder) {
    this.description = builder.description;
    this.commandOptions = builder.commandOptions;
    this.callback = builder.callback;
    this.botEventSupplier = builder.botEventSupplier;
  }

  public static class Builder {
    private String description;
    private List<OptionData> commandOptions;
    private Consumer<SlashCommandInteractionEvent> callback;
    private Supplier<BotEvent> botEventSupplier;

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

    public Builder setBotEventSupplier(Supplier<BotEvent> botEventSupplier) {
      this.botEventSupplier = botEventSupplier;
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
