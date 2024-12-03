package com.victorchimenton.command;

import com.victorchimenton.service.PaymentService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.math.BigDecimal;

public class GenerateQrCodeCommand {

    private final PaymentService paymentService = new PaymentService();

    public void execute(SlashCommandInteractionEvent event) {
        try {
            var email = event.getOption("email").getAsString();
            var amount = event.getOption("amount").getAsDouble();

            var payment = paymentService.createPayment(BigDecimal.valueOf(amount), email);
            var qrCode = paymentService.createQrCodeImage(payment);


        } catch (Exception exception) {
            event.reply("Erro ao processar pagamento").setEphemeral(true).queue();
            System.out.println("Erro ao processar pagamento: " + exception.getMessage());
        }
    }
}
