package com.github.lowestofthe1ow.misakibotjava.botevents;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class BotEvent {
  /** A list of all participants in the event */
  public final List<User> participants = new ArrayList<User>();
  /** A list of all messages that are "listening" for user input */
  public final List<Message> listeningMessages = new ArrayList<Message>();

  /** The name of the event */
  public String eventName;

  public abstract void handleButton(ButtonInteractionEvent event);
  protected abstract EmbedBuilder buildEmbed();

  BotEvent() {
  }
}
