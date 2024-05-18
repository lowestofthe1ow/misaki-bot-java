package com.github.lowestofthe1ow.misakibotjava.botevents;

import com.github.lowestofthe1ow.misakibotjava.util.RandomString;

import com.github.lowestofthe1ow.misakibotjava.slashcommands.CommandHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;
import java.util.List;

/* TODO: Refactor this entire class, this kinda looks horrible */
public class RPS extends BotEvent {
  /** An array list containing the participant's game choices */
  private final List<String> participantChoices = Arrays.asList(new String[] {
      "", ""
  });;

  /** 
   * Builds the embed for the RPS event 
   * @return EmbedBuilder object containing event information
   */
  @Override
  public EmbedBuilder buildEmbed() {
    final EmbedBuilder embed = new EmbedBuilder();

    embed.setTitle("Rock, paper, scissors!");
    embed.setDescription(RandomString.randomize("Let's play a quick game!", "Winner gets nothing!", "遊ぶにゃん～")
        + "\nBoth have **5 seconds** to make a choice, then I'll reveal the result!");
    embed.addField("Participants", participants.get(0).getAsMention() + " and " + participants.get(1).getAsMention(),
        false);
    /* TODO: Replace image */
    embed.setThumbnail("https://cdn.discordapp.com/avatars/1234044926593859644/db71162f1973ad3324b5083d74ffe8d2.png");

    return embed;
  }

  /** 
   * Handle the processing of the button interaction
   * @param event The ButtonInteractionEvent detected by the listener
   */
  @Override
  public void handleButton(ButtonInteractionEvent event) {
    switch (event.getComponentId()) {
    /* Buttons for player game choices */
    case "event.rps.rock":
    case "event.rps.paper":
    case "event.rps.scissors":
      if (participants.contains(event.getUser()))
        makeChoice(event);
      else
        event.reply("Psst. You're not participating in this event.").setEphemeral(true).queue();
      break;

    /* Button for confirming challenge */
    case "event.rps.yes":
      /* Index 1 of participants contains the opponent being challenged */
      if (participants.get(1).equals(event.getUser()))
        beginGame(event);
      else
        event.reply("Psst. You... weren't the one being challenged.").setEphemeral(true).queue();
      break;

    /* Button for declining challenge (or cancelling) */
    case "event.rps.no":
      if (participants.contains(event.getUser())) {
        event.deferEdit().queue();
        listeningMessages.remove(event.getMessage());
        callingHandler.timeOut(event.getMessage(), "Oh, nevermind then...");
      }
    }
  }

  /** 
   * Determine the game result and construct a message to display
   * @return Text string to display when the game ends
   */
  private String buildResultString() {
    String outcome;

    /* Outcome: draw */
    if (participantChoices.get(0).equals(participantChoices.get(1)))
      return "Both players chose **" + participantChoices.get(0) + "**! It's a draw!";

    /* Outcome: Player 2 victory */
    if (participantChoices.get(0).equals("rock") && participantChoices.get(1).equals("paper")
        || participantChoices.get(0).equals("paper") && participantChoices.get(1).equals("scissors")
        || participantChoices.get(0).equals("scissors") && participantChoices.get(1).equals("rock"))
      outcome = participants.get(1).getAsMention() + " wins!";
    /* Outcome: Player 1 victory */
    else
      outcome = participants.get(0).getAsMention() + " wins!";

    return "**" + participants.get(0).getName() + "** chose **" + participantChoices.get(0) + "**, while **"
        + participants.get(1).getName() + "** chose **" + participantChoices.get(1) + "**, so " + outcome;
  }

   /** 
   * Handle the processing of a user's game choice
   * @param event The ButtonInteractionEvent detected by the listener
   */
  public void makeChoice(ButtonInteractionEvent event) {
    /* Sets the user's choice. The substring(10) call eliminates the prefix in the button ID */
    participantChoices.set(participants.indexOf(event.getUser()), event.getComponentId().substring(10));
    event.reply("よし！You choose **" + event.getComponentId().substring(10) + "**, yeah?").setEphemeral(true).queue();
  }

  /** 
   * Handle the processing of the opponent accepting the user's challenge
   * @param event The ButtonInteractionEvent detected by the listener
   */
  public void beginGame(ButtonInteractionEvent event) {
    /* Reset the commandHandler's Timer object */
    callingHandler.resetTimer();

    event.replyEmbeds(buildEmbed().build())
        .addActionRow(Button.primary("event.rps.rock", "Rock!"), Button.primary("event.rps.paper", "Paper!"),
            Button.primary("event.rps.scissors", "Scissors!"))
        .queue((hookObject) -> hookObject.retrieveOriginal().queue((messageObject) -> {
          /* Add message to listening messages list then schedule a timer */
          listeningMessages.add(messageObject);
          scheduleHandlerTimer(5000, () -> {
            /* Remove message from messages list */
            listeningMessages.remove(messageObject);
            if (!participantChoices.get(0).isEmpty() && !participantChoices.get(1).isEmpty())
              callingHandler.timeOut(messageObject, buildResultString());
            else
              callingHandler.timeOut(messageObject, "...Eh? Someone forgot to make their choice!");
          });
        }));

    /* Remove all buttons from the Message object */
    event.getMessage().editMessageComponents().queue();
  }

  /** 
   * Handle the event's slash command by sending a challenge message to the specified user
   * @param event The SlashCommandInteractionEvent detected by the listener
   */
  public void challengePrompt(SlashCommandInteractionEvent event) {
    /* Acknowledge the interaction with an epehemeral reply */
    event.reply("\'Kaaay, I'll ping **" + event.getOption("opponent").getAsUser().getName() + "** for you!")
        .setEphemeral(true).queue();

    /* Send the challenge */
    event.getChannel()
        .sendMessage("お～い、" + participants.get(1).getAsMention() + "！**" + participants.get(0).getName()
            + "** is challenging you to rock-paper-scissors! You haaave **15 seconds** to give an answer!")
        .addActionRow(Button.success("event.rps.yes", "Accept"), Button.danger("event.rps.no", "Deny"))
        .queue((messageObject) -> {
          /* Add message to listening messages list then schedule a timer */
          listeningMessages.add(messageObject);
          scheduleHandlerTimer(15000, () -> {
            /* Remove message from messages list */
            listeningMessages.remove(messageObject);
            callingHandler.timeOut(messageObject,
                RandomString.randomize("Oh nooo, they didn't respond. Whatever will I do?",
                    "You're leaving them hanging? Ruuude.", "Ah, guess they're not arooound."));
          });
        });
  }

  public RPS(CommandHandler handler, User user, User opponent) {
    super(handler);
    eventName = "RPS";

    /* Add user and opponent to participants array */
    participants.add(user);
    participants.add(opponent);
  }
}