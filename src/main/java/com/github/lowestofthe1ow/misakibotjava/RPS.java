package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class RPS {
    public void beginGame(int playerChoice, ButtonInteractionEvent event) {
        String choices[] = { "rock", "paper", "scissors" };
        String outcomes[] = { "Guess it's a draw, huh?", "Guess I win this time!", "Guess you win this time!" };

        int outcome = (int) (Math.random() * 3);

        event.reply("いくよ～！Rock, paper, scissors!\nI choose **" + choices[(playerChoice + outcome) % 3]
                + "**. If you chose **" + choices[playerChoice] + "**, then...\n" + outcomes[outcome])
                .queue();
    }
}
