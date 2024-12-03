package com.victorchimenton.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "gerarqrcode" -> new GenerateQrCodeCommand().execute(event);
            default -> event.reply("Comando não reconhecido!").setEphemeral(true).queue();
        }
    }
}
