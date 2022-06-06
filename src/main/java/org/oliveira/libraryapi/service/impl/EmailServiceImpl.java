package org.oliveira.libraryapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.oliveira.libraryapi.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(List<String> listaEmails, String mensage) {
        String[] emails = listaEmails.toArray(new String[listaEmails.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(mensage);

        mailMessage.setTo(emails);

        javaMailSender.send(mailMessage);
    }
}
