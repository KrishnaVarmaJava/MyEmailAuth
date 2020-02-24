package com.springsecurity.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.springsecurity.demo.model.ConfirmationToken;
import com.springsecurity.demo.model.User;



@Service("emailSenderService")
public class EmailSenderService {

  private JavaMailSender javaMailSender;

  @Autowired
  public EmailSenderService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }
  
  @Value("${spring.mail.username}")
  private String userName;
  
  @Async
  public void sendEmail(User user,ConfirmationToken confirmationToken) throws Exception,MailAuthenticationException {
	  
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmailId());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom(userName);
		mailMessage.setText("To confirm your account, please click here : "
		+"http://localhost:8082/user/confirm-account?token="+confirmationToken.getConfirmationToken());	

		javaMailSender.send(mailMessage);
  }
}