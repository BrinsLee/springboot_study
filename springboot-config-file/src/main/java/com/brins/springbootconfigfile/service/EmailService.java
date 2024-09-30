package com.brins.springbootconfigfile.service;

public interface EmailService {
    boolean sendMail(String to, String title, String content);
}
