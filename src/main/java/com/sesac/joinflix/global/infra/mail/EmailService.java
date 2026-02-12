package com.sesac.joinflix.global.infra.mail;

public interface EmailService {

    void sendEmail(String to, String subject, String text);
}
