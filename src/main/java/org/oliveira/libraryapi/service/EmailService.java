package org.oliveira.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendMails(List<String> listaEmails, String mensage);
}
