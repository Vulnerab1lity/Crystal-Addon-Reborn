package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class CheckSSLCommand extends Command {
    public CheckSSLCommand() {
        super("checkssl", "Check the SSL certificate of a domain.", "checkssl");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("domain", StringArgumentType.greedyString())
                .executes(context -> {
                    String domain = context.getArgument("domain", String.class);

                    try {
                        SSLInfo sslInfo = getSSLInfo(domain);

                        info("SSL Certificate Information for " + domain);
                        info("Issuer: " + sslInfo.issuer());
                        info("Subject: " + sslInfo.subject());
                        info("Validity: " + sslInfo.validity());
                    } catch (IOException e) {
                        error("An error occurred while checking the SSL certificate.");
                    }
                    return SINGLE_SUCCESS;
                })
        );
    }

    private SSLInfo getSSLInfo(String domain) throws IOException {
        URL url = new URL("https://" + domain);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.connect();
        Certificate[] certs = connection.getServerCertificates();

        X509Certificate x509Certificate = (X509Certificate) certs[0];

        String issuer = x509Certificate.getIssuerX500Principal().getName();
        String subject = x509Certificate.getSubjectX500Principal().getName();
        String validity = x509Certificate.getNotBefore() + " - " + x509Certificate.getNotAfter();
        return new SSLInfo(issuer, subject, validity);
    }

    private record SSLInfo(String issuer, String subject, String validity) {
    }
}