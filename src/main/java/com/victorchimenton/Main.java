package com.victorchimenton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends ListenerAdapter {

    public enum Product {
        PRODUTO_A("Produto A"),
        PRODUTO_B("Produto B"),
        PRODUTO_C("Produto C");

        private final String name;

        Product(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public static void main(String[] args) {
        MercadoPagoConfig.setAccessToken(System.getenv("MPSDK"));

        JDABuilder.createLight(System.getenv("TOKENJDA"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("Under development"))
                .addEventListeners(new Main())
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        String contentRaw = event.getMessage().getContentRaw();

        if (contentRaw.equalsIgnoreCase("!start")) {
            event.getChannel().sendMessageEmbeds(
                    createEmbed(
                            "BalaStore",
                            "Clique no botão abaixo para adquirir um serviço ou realizar um orçamento"
                    )
            ).setActionRow(
                    Button.primary("comprar", "COMPRAR"),
                    Button.secondary("orcamento", "ORÇAMENTO")
            ).queue();
        }

        if (contentRaw.startsWith("!criar-pix")) {
            String[] args = contentRaw.split(" ");
            if (args.length == 2) {
                try {
                    double valor = Double.parseDouble(args[1]);

                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("Por favor, forneça um valor válido para o Pix.").queue();
                }
            } else {
                event.getChannel().sendMessage("Uso correto: !criar-pix <valor>").queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("comprar")) {
            SelectMenu menu = StringSelectMenu.create("selecao-produto")
                    .setPlaceholder("Selecione um Produto")
                    .addOption("A", "produto_a", "Description A")
                    .addOption("B", "produto_b", "Description B")
                    .addOption("C", "produto_c", "Description C")
                    .build();

            event.reply("Selecione o produto")
                    .addActionRow(menu)
                    .setEphemeral(true)
                    .queue();
        } else if (event.getComponentId().equals("orcamento")) {

            if (hasOpenChannel(event.getGuild(), event.getMember(), "Orçamentos")) {
                event.reply("Você já possui um canal aberto nesta categoria.'")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            TextChannel channel = createPrivateChannel(event.getGuild(), event.getMember(), "Orçamento", "Orçamentos");
            event.reply("Um canal exclusivo foi criado para você: " + channel.getAsMention() + " \nDetalhe seu pedido para que possamos atender.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("product-selection")) {
            String selectedProduct = event.getValues().get(0);
            Product product = Product.valueOf(selectedProduct.toUpperCase());

            if (hasOpenChannel(event.getGuild(), event.getMember(), "Orçamentos")) {
                event.reply("Você já possui um canal aberto nesta categoria.'")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            try {
                String qrCodeText = createPayment(new BigDecimal(10.0), "pepinha@peppa.com");
                byte[] qrCodeImage = createQrCodeImage(qrCodeText);

                TextChannel channel = createPrivateChannel(event.getGuild(), event.getMember(), product.getName(), "Compras");

                channel.sendMessage("")
                        .addFiles(FileUpload.fromData(qrCodeImage, "pix_qr_code.png"))
                        .queue();

                event.reply("Um canal exclusivo foi criado para você discutir sobre **" + product.getName() + "**: " + channel.getAsMention())
                        .setEphemeral(true)
                        .queue();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TextChannel createPrivateChannel(Guild guild, Member member, String topic, String categoryName) {
        EnumSet<Permission> permissions = EnumSet.of(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_HISTORY
        );

        EnumSet<Permission> denyPerm = EnumSet.noneOf(Permission.class);

        Category category = guild.getCategoriesByName(categoryName, true).stream()
                .findFirst()
                .orElseGet(() -> guild.createCategory(categoryName).complete());

        return category.createTextChannel(categoryName.toLowerCase() + "-" + member.getEffectiveName().toLowerCase().replace(" ", "-"))
                .addPermissionOverride(guild.getPublicRole(), denyPerm, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(member, permissions, denyPerm)
                .setTopic("Canal exclusivo para: " + topic)
                .complete();
    }

    private boolean hasOpenChannel(Guild guild, Member member, String categoryName) {
        return guild.getCategoriesByName(categoryName, true).stream()
                .flatMap(category -> category.getTextChannels().stream())
                .anyMatch(channel -> channel.getTopic() != null && channel.getTopic().contains(member.getEffectiveName()));
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


    private String createPayment(BigDecimal valor, String emailPagador) throws Exception {
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", "UNIQUE_KEY_" + System.currentTimeMillis());

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        PaymentClient client = new PaymentClient();

        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(valor)
                .paymentMethodId("pix")
                .externalReference(UUID.randomUUID().toString())
                .dateOfExpiration(OffsetDateTime.now().plusMinutes(15))
                .payer(PaymentPayerRequest.builder()
                        .email(emailPagador)
                        .build())
                .build();

        try {
            Payment payment = client.create(paymentCreateRequest, requestOptions);

            if (payment.getPointOfInteraction() != null
                    && payment.getPointOfInteraction().getTransactionData() != null) {
                return payment.getPointOfInteraction().getTransactionData().getQrCode();
            } else {
                throw new Exception("Erro ao gerar o QR Code ou criar o pagamento.");
            }
        } catch (MPApiException e) {
            System.out.println("Erro ao criar pagamento: " + e.getMessage());

            if (e.getApiResponse() != null) {
                System.out.println("Detalhes do erro:");
                System.out.println("Status Code: " + e.getApiResponse().getStatusCode());
                System.out.println("Response Body: " + e.getApiResponse().getContent());
            } else {
                System.out.println("Sem detalhes da resposta.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private byte[] createQrCodeImage(String url) throws WriterException {
        int imgSize = 200;
        BitMatrix matrix = new MultiFormatWriter().encode(
                url,
                BarcodeFormat.QR_CODE,
                imgSize,
                imgSize
        );

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(
                    matrix,
                    "png",
                    byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
