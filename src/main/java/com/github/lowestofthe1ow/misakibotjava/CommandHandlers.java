package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CommandHandlers {
    public void say(SlashCommandInteractionEvent event) {
        event.reply("わかった！I'll go say that!").setEphemeral(true).queue();
        event.getChannel().sendMessage(event.getOption("content").getAsString()).queue();
    }

    public void rps(SlashCommandInteractionEvent event) {
        event.reply("ねぇ、" + event.getUser().getName() + "さん！Let's play a quick game!")
                .addActionRow(
                        Button.primary("rock", "Rock!"), 
                        Button.primary("paper", "Paper!"),
                        Button.primary("scissors", "Scissors!"))
                .queue();
    }

    public void executeHandler(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                say(event);
                break;
            case "rps":
                rps(event);
                break;
        }
    }
}
