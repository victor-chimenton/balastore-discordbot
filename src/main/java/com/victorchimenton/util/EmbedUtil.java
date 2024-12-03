package com.victorchimenton.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class EmbedUtil {

    private static final Color DEFAULT_COLOR = Color.BLUE;

    /**
     * Cria um EmbedBuilder com as configurações padrão.
     *
     * @param title   Título do embed.
     * @param content Conteúdo principal do embed.
     * @return EmbedBuilder configurado.
     */
    public static EmbedBuilder createDefaultEmbed(String title, String content) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(content)
                .setColor(DEFAULT_COLOR)
                .setTimestamp(Instant.now());
    }

    /**
     * Cria um embed personalizado.
     *
     * @param title   Título do embed.
     * @param content Conteúdo principal do embed.
     * @param color   Cor personalizada.
     * @return EmbedBuilder configurado.
     */
    public static EmbedBuilder createCustomEmbed(
            String title,
            String content,
            Color color,
            String image,
            String footer
    ) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(content)
                .setColor(color)
                .setThumbnail(image)
                .setFooter(footer, image)
                .setTimestamp(Instant.now());
    }

    /**
     * Adiciona um campo ao embed.
     *
     * @param embedBuilder EmbedBuilder existente.
     * @param name         Nome do campo.
     * @param value        Valor do campo.
     * @param inline       Se o campo será exibido inline.
     */
    public static void addField(EmbedBuilder embedBuilder, String name, String value, boolean inline) {
        embedBuilder.addField(name, value, inline);
    }

    /**
     * Converte um EmbedBuilder para MessageEmbed.
     *
     * @param embedBuilder EmbedBuilder a ser convertido.
     * @return MessageEmbed pronto para ser enviado.
     */
    public static MessageEmbed build(EmbedBuilder embedBuilder) {
        return embedBuilder.build();
    }
}
