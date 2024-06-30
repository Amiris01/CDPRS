package com.SmartHealthRemoteSystem.SHSR.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.Mail.MailStructure;
import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Service
public class MailService {
  @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository

    public void sendNewUserMail(String mail, MailStructure mailStructure) {
        // Check if the email already exists
        if (emailExists(mail)) {
            System.out.println("Error: Email " + mail + " already exists. Email not sent.");
            return;
        }

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(fromMail);
            simpleMailMessage.setSubject("Welcome to CDPRSystem, you have been registered as a new user");
            simpleMailMessage.setText("Refer the following details to login. " 
             +"Temporary password : "+ mailStructure.getPassword());
            simpleMailMessage.setTo(mail);

            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            // Handle mail sending exceptions, e.g., log the error
            // This is important for robustness and debugging
            e.printStackTrace();
        }
    }

    // Method to check if email already exists in the user repository
    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendAssignedMail(String mail, String subject, String message, MailStructure mailStructure){
        

        try {
             SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(mail);

        mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            // Log the exception
           e.printStackTrace();
        }
       
        
    }
    public void sendUnassignedMail(String mail, String subject, String message, MailStructure mailStructure){
        

        try {
             SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(mail);

        mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            // Log the exception
           e.printStackTrace();
        }
       
        
        
    }
    public void sendReleasedMail(String mail, String subject, String message, MailStructure mailStructure){
        

        try {
             SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(mail);

        mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            // Log the exception
           e.printStackTrace();
        }
       
        
    }

}