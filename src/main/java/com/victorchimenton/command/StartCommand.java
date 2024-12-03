package com.victorchimenton.command;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.OffsetDateTime;

public class StartCommand {

    public void execute(SlashCommandInteractionEvent event) {
        event.getChannel().sendMessageEmbeds(
                createEmbed(
                        "Balastore",
                        "Clique no botão abaixo para adquirir um serviço ou realizar um orçamento"

                )
        ).setActionRow(
                Button.primary("comprar", "COMPRAR"),
                Button.secondary("orcamento", "ORÇAMENTO"),
                Button.link("https://balafini.live", "SITE")
        ).queue();
    }

    private MessageEmbed createEmbed(String title, String description) {
        return new MessageEmbed(
                "https://balafini.live",
                title,
                description,
                EmbedType.ARTICLE,
                OffsetDateTime.now(),
                0x0062ff,
                null,
                null,
                new MessageEmbed.AuthorInfo(
                        "BalaBot",
                        "https://balafini.live",
                        "https://imgur.com/DHA7Kc1.jpeg",
                        ""
                ),
                null,
                new MessageEmbed.Footer("Copyright © 2024, Balastore. All Rights Reserved.", "https://imgur.com/DHA7Kc1.jpeg", ""),
                new MessageEmbed.ImageInfo("https://imgur.com/DHA7Kc1.jpeg", "", 10, 10),
                null
        );
    }
}

