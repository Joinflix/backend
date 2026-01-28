package com.sesac.joinflex.global.infra.mail;

public interface EmailService {

    void sendEmail(String to, String subject, String text);
}
