package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Timer;

public class CommandHandler {
  /** The Timer object tracking the handler's timeouts */
  public Timer handlerTimer;

  /** The main App object that created the handler */
  private App callingApp;

  /**
   * Resets the handlerTimer field
   */
  public void resetTimer() {
    handlerTimer.cancel();
    handlerTimer.purge();
    handlerTimer = null;
  }

  /**
   * Times out the event and removes it from the ongoing events list. Sends a reply to a message and strips that message
   * of all Components (including ActionRow Buttons)
   * 
   * @param messageObject The Message object to send a reply to and reset
   * @param timeOutText The content of the reply
   */
  public void timeOut(Message messageObject, String timeOutText) {
    messageObject.reply(timeOutText).queue();
    messageObject.editMessageComponents().queue();

    timeOut();
  }

  /**
   * Times out the event and removes it from the ongoing events list
   */
  public void timeOut() {
    BotEvent botEvent = callingApp.ongoingBotEvents.stream().filter((result) -> result.callingHandler == this)
        .findFirst().orElse(null);

    if (botEvent != null)
      callingApp.ongoingBotEvents.remove(botEvent);

    resetTimer();
  }

  /**
   * Executes the callback tied to the received SlashCommandInteractionEvent based on the hash table in callingApp
   * 
   * @param event The SlashCommandInteractionEvent detected by the listener
   */
  public void executeHandler(SlashCommandInteractionEvent event) {
    callingApp.commandList.commandHash.get(event.getName()).handlerCallback.accept(event, this);
  }

  CommandHandler(App callingApp) {
    this.callingApp = callingApp;
  }
}
