package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

abstract class BotEvent {
  /** A list of all participants in the event */
  public final List<User> participants = new ArrayList<User>();
  /** A list of all messages that are "listening" for user input */
  public final List<Message> listeningMessages = new ArrayList<Message>();

  /** The name of the event */
  public String eventName;
  /** The CommandHandler calling this BotEvent. This is the same CommandHandler paired to the event in ongoingBotEvents */
  public CommandHandler callingHandler;

  abstract void handleButton(ButtonInteractionEvent event);
  abstract EmbedBuilder buildEmbed();

  /** 
   * Create a new Timer object for the callingHandler's handlerTimer field and schedule a TimerTask
   * @param time The timer duration in milliseconds
   * @param callback The Runnable functional interface to execute when the timer runs out
   */
  public void scheduleHandlerTimer(long time, Runnable callback) {
    callingHandler.handlerTimer = new Timer();
    callingHandler.handlerTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        callback.run();
      }
    }, time);
  };

  BotEvent(CommandHandler handler) {
    callingHandler = handler;
  }
}
