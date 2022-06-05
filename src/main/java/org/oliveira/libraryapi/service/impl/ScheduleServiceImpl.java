package org.oliveira.libraryapi.service.impl;

import lombok.RequiredArgsConstructor;

import org.oliveira.libraryapi.model.entity.Loan;
import org.oliveira.libraryapi.service.EmailService;
import org.oliveira.libraryapi.service.LoanService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String mensagem;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();

        List<String> listaEmails = allLateLoans.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());


        emailService.sendMails(listaEmails, mensagem);

    }
}
