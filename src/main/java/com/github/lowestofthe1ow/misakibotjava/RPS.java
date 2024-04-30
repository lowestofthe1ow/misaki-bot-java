package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;
import java.util.List;

public class RPS extends BotEvent {
  List<String> participantChoices;

  @Override
  public EmbedBuilder buildEmbed() {
    EmbedBuilder embed = new EmbedBuilder();

    embed.setTitle("Rock, paper, scissors!");
    embed.setDescription(RandomString.randomize("Let's play a quick game!", "Winner gets nothing!", "遊ぶにゃん～")
        + "\nBoth have **5 seconds** to make a choice, then I'll reveal the result!");
    embed.addField("Participants", participants.get(0).getAsMention() + " and " + participants.get(1).getAsMention(),
        false);
    embed.setThumbnail("https://cdn.discordapp.com/avatars/1234044926593859644/db71162f1973ad3324b5083d74ffe8d2.png");

    return embed;
  }

  public void makeChoice(ButtonInteractionEvent event) {
    if (!participants.contains(event.getUser())) {
      event.reply("Psst. You're not participating in this event.").setEphemeral(true).queue();
      return;
    }

    participantChoices.set(participants.indexOf(event.getUser()), event.getComponentId().substring(10));
    event.reply("よし！You choose **" + event.getComponentId().substring(10) + "**, yeah?").setEphemeral(true).queue();
  }

  /* TODO: Refactor */
  private String buildResultString() {
    String selected;
    String outcome;

    if (participantChoices.get(0).equals(participantChoices.get(1))) {
      selected = "Both players chose **" + participantChoices.get(0) + "**! ";
      outcome = "It's a draw!";
    } else {
      selected = "**" + participants.get(0).getName() + "** chose **" + participantChoices.get(0) + "**, while **"
          + participants.get(1).getName() + "** chose **" + participantChoices.get(1) + "**, so ";

      if (participantChoices.get(0).equals("rock") && participantChoices.get(1).equals("paper")
          || participantChoices.get(0).equals("paper") && participantChoices.get(1).equals("scissors")
          || participantChoices.get(0).equals("scissors") && participantChoices.get(1).equals("rock"))
        outcome = participants.get(1).getAsMention() + " wins!";
      else
        outcome = participants.get(0).getAsMention() + " wins!";
    }

    return selected + outcome;
  }

  public void beginGame(ButtonInteractionEvent event) {
    if (!participants.get(1).equals(event.getUser())) {
      event.reply("Psst. You... weren't the one being challenged.").setEphemeral(true).queue();
      return;
    }

    callingHandler.resetTimer();

    event.replyEmbeds(buildEmbed().build())
        .addActionRow(Button.primary("event.rps.rock", "Rock!"), Button.primary("event.rps.paper", "Paper!"),
            Button.primary("event.rps.scissors", "Scissors!"))
        .queue((hookObject) -> hookObject.retrieveOriginal().queue((messageObject) -> {
          scheduleHandlerTimer(messageObject, 5000, () -> {
            if (!participantChoices.get(0).isEmpty() && !participantChoices.get(1).isEmpty())
              callingHandler.timeOut(messageObject, buildResultString());
            else
              callingHandler.timeOut(messageObject, "...Eh? Someone forgot to make their choice!");
          });
        }));

    event.getMessage().editMessageComponents().queue();
  }

  public void challengePrompt(SlashCommandInteractionEvent event) {
    event.reply("\'Kaaay, I'll ping **" + event.getOption("opponent").getAsUser().getName() + "** for you!")
                .setEphemeral(true).queue();
    event.getChannel()
        .sendMessage("お～い、" + participants.get(1).getAsMention() + "！**" + participants.get(0).getName()
            + "** is challenging you to rock-paper-scissors! You haaave **15 seconds** to give an answer!")
        .addActionRow(Button.success("event.rps.yes", "Accept"), Button.danger("event.rps.no", "Deny"))
        .queue((messageObject) -> scheduleHandlerTimer(messageObject, 15000,
            () -> callingHandler.timeOut(messageObject,
                RandomString.randomize("Oh nooo, they didn't respond. Whatever will I do?",
                    "You're leaving them hanging? Ruuude.", "Ah, guess they're not arooound."))));
  }

  RPS(CommandHandler handler, User user, User opponent) {
    super(handler);

    eventName = "RPS";
    participantChoices = Arrays.asList(new String[] {"", ""});

    participants.add(user);
    participants.add(opponent);
  }
}

/*
 * public class RPS { String choices[] = { "rock", "paper", "scissors" }; String
 * outcomes[] = { "Guess it's a draw, huh?", "Guess I win this time!",
 * "Guess you win this time!" };
 * 
 * private int findChoice(String playerChoice) { switch (playerChoice) {
 * default: case "event.rps.rock": return 0; case "event.rps.paper": return 1;
 * case "event.rps.scissors": return 2; } }
 * 
 * public void beginGame(String playerChoice, ButtonInteractionEvent event) {
 * int outcome = new Random().nextInt(3); int playerChoiceIndex =
 * findChoice(playerChoice);
 * 
 * event.reply("いくよ～！Rock, paper, scissors!\nI choose **" +
 * choices[(playerChoiceIndex + outcome) % 3] + "**. If you chose **" +
 * choices[playerChoiceIndex] + "**, then...\n" + outcomes[outcome]) .queue(); }
 * }
 */
