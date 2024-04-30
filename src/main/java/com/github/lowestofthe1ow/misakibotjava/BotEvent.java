package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

abstract class BotEvent {
  List<User> participants;
  String eventName;
  CommandHandler callingHandler;

  abstract EmbedBuilder buildEmbed();

  public void scheduleHandlerTimer(Message messageObject, long time, Runnable callback) {
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
    participants = new ArrayList<User>();
  }
}
