package com.github.lowestofthe1ow.misakibotjava;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class App extends ListenerAdapter {
    public static void main(String[] arguments) throws Exception {
        JDA api = JDABuilder.createDefault("MTIzNDA0NDkyNjU5Mzg1OTY0NA.GxB5vi.LLT2FVhEPb_ecmqYXtWrBoUFjFYP_5dsRaI-3s")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new App())
                .build()
                .awaitReady();

        CommandListUpdateAction commands = api.updateCommands();

        commands.addCommands(
                Commands.slash("say", "Make me say something and bait lowest into doing whatever you want!")
                        .addOption(STRING, "content", "Your message", true),
                Commands.slash("rps", "Challenge me to rock-paper-scissors!"));

        commands.queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw(); 

        if (content.contains("<@1234044926593859644>"))
        {
            event.getMessage().reply("Pong!").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null)
            return;
        
        CommandHandlers handlers = new CommandHandlers();
        handlers.executeHandler(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "rock":
                new RPS().beginGame(0, event);
                break;
            case "paper":
                new RPS().beginGame(1, event);
                break;
            case "scissors":
                new RPS().beginGame(2, event);
                break;
        }
        event.getMessage().editMessageComponents().queue();
    }
}