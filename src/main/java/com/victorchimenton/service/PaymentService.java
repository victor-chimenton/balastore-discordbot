package com.victorchimenton.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class PaymentService {

    static {
        MercadoPagoConfig.setAccessToken(System.getenv("MPSDK"));
    }

    @SneakyThrows
    public String createPayment(BigDecimal amount, String payerEmail) {
        try {
            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .transactionAmount(amount)
                    .paymentMethodId("pix")
                    .externalReference(UUID.randomUUID().toString())
                    .dateOfExpiration(OffsetDateTime.now().plusMinutes(15))
                    .payer(PaymentPayerRequest.builder()
                            .email(payerEmail)
                            .build())
                    .build();

            PaymentClient paymentClient = new PaymentClient();
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(Map.of("x-idempotency-key", "UNIQUE_KEY_" + System.currentTimeMillis()))
                    .build();

            Payment payment = paymentClient.create(paymentRequest, requestOptions);

            if (payment.getPointOfInteraction() != null
                    && payment.getPointOfInteraction().getTransactionData() != null) {
                return payment.getPointOfInteraction().getTransactionData().getQrCode();
            } else {
                throw new Exception("Erro ao gerar o QR Code.");
            }

        } catch (MPApiException exception) {
            System.out.println("Erro ao criar pagamento: " + exception.getMessage());
            if (exception.getApiResponse() != null) {
                System.out.println("Detalhes do erro:");
                System.out.println("Status Code: " + exception.getApiResponse().getStatusCode());
                System.out.println("Response Body: " + exception.getApiResponse().getContent());
            }
            throw exception;
        } catch (Exception exception) {
            System.out.println("Erro inesperado: " + exception.getMessage());
            throw new Exception("Erro ao gerar pagamento", exception);
        }
    }

    @SneakyThrows
    public byte[] createQrCodeImage(String qrCodeText) {
        int size = 150;
        var matrix = new MultiFormatWriter().encode(
                qrCodeText,
                BarcodeFormat.QR_CODE,
                size,
                size
        );

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

}
