package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Timer;

public class CommandHandler {
  Timer handlerTimer;
  App callingApp;

  public void resetTimer() {
    handlerTimer.cancel();
    handlerTimer.purge();
    handlerTimer = null;
  }

  public void timeOut(Message messageObject, String timeOutText) {
    messageObject.reply(timeOutText).queue();
    callingApp.ongoingBotEvent = null;

    resetTimer();
    messageObject.editMessageComponents().queue();
  }

  public void executeHandler(SlashCommandInteractionEvent event) {
    if (callingApp.commandList.commandHash.get(event.getName()).isEvent == true)
      if (callingApp.ongoingBotEvent != null)
        event.reply("Um, whoops. This event is still ongoing. Wait for it to finish, alright?")
            .addEmbeds(callingApp.ongoingBotEvent.buildEmbed().build()).setEphemeral(true).queue();
    callingApp.commandList.commandHash.get(event.getName()).handlerCallback.accept(event, this);
  }

  CommandHandler(App app) {
    callingApp = app;
  }
}
