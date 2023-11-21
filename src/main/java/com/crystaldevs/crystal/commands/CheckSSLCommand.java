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
                    info("Issuer: " + sslInfo.getIssuer());
                    info("Subject: " + sslInfo.getSubject());
                    info("Validity: " + sslInfo.getValidity());
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

        String issuer = x509Certificate.getIssuerDN().getName();
        String subject = x509Certificate.getSubjectDN().getName();
        String validity = x509Certificate.getNotBefore() + " - " + x509Certificate.getNotAfter();

        return new SSLInfo(issuer, subject, validity);
    }

    private static class SSLInfo {
        private final String issuer;
        private final String subject;
        private final String validity;

        public SSLInfo(String issuer, String subject, String validity) {
            this.issuer = issuer;
            this.subject = subject;
            this.validity = validity;
        }

        public String getIssuer() {
            return issuer;
        }

        public String getSubject() {
            return subject;
        }

        public String getValidity() {
            return validity;
        }
    }
}
