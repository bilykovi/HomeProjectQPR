package pl.luxoft.qpr.bilykov.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import pl.luxoft.qpr.bilykov.exception.UserServiceException;
import pl.luxoft.qpr.bilykov.repository.ChangePasswordRequestRepository;
import pl.luxoft.qpr.bilykov.repository.RoleRepository;
import pl.luxoft.qpr.bilykov.repository.UserRepository;
import pl.luxoft.qpr.bilykov.dto.ChangePasswordRequest;
import pl.luxoft.qpr.bilykov.dto.UserRequest;
import pl.luxoft.qpr.bilykov.dto.templates.EmailTemplateCommon;
import pl.luxoft.qpr.bilykov.dto.templates.ResetPasswordTemplate;
import pl.luxoft.qpr.bilykov.email.EmailSenderImpl;
import pl.luxoft.qpr.bilykov.exception.UserServiceException;
import pl.luxoft.qpr.bilykov.model.User;
import pl.luxoft.qpr.bilykov.repository.ChangePasswordRequestRepository;
import pl.luxoft.qpr.bilykov.repository.RoleRepository;
import pl.luxoft.qpr.bilykov.repository.UserRepository;
import pl.luxoft.qpr.bilykov.service.MessageService;
import pl.luxoft.qpr.bilykov.service.UserService;

@Component
public class UserServiceDefault implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    MessageService messageSource;

    @Autowired
    EmailSenderImpl emailSenderImpl;
    
    @Autowired
    ChangePasswordRequestRepository changePasswordRequestRepository;


    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserServiceException(messageSource.getMessage("err.email.not.exists"));
        }
        return user;
    }

    public void register(UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            throw new UserServiceException(messageSource.getMessage("err.user.already.exists"));
        }
        user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUserRole(roleRepository.findByName("user"));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String email) {
        deleteUser(userRepository.findByEmail(email));
    }

    @Override
    public void deleteUser(int id) {
        deleteUser(userRepository.findOne(id));
    }
    
    private void deleteUser(User user) {
        if (user == null) {
            throw new UserServiceException(messageSource.getMessage("err.user.not.found"));
        }

        String currentUser = getCurrentUser();
        if (currentUser != null && currentUser.equals(user.getEmail())) {
            userRepository.delete(user.getUserId());
            SecurityContextHolder.getContext().setAuthentication(null);
        } else {
            throw new UserServiceException(messageSource.getMessage("err.operation.not.allowed"));
        }
    }

    public String getCurrentUser() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    @Override
    public void sendPasswordRequestEmail(String email, String baseUrl) {
         final Integer TOKEN_LIFE_TIME_IN_HOURS = 
                 ChangePasswordRequestRepository.TOKEN_LIFE_TIME_IN_HOURS;
         final String SUBJECT = "Homebudget: password recovery request";
         final String TEMPLATE_NAME = "password-recovery";
         
         User user = getUser(email);
         EmailTemplateCommon templateInfo = new ResetPasswordTemplate();
         templateInfo.setSubject(SUBJECT);
         templateInfo.setTo(user.getEmail());
         templateInfo.setTemplateName(TEMPLATE_NAME);
         templateInfo.setParameter("tokenLifeTime", TOKEN_LIFE_TIME_IN_HOURS);
         templateInfo.setParameter("recoveryLink", generateRecoveryLink(email));
         templateInfo.setParameter("baseUrl", baseUrl);
         
         emailSenderImpl.send(templateInfo, Locale.getDefault());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = changePasswordRequestRepository.checkTokenValidityAndGetEmail(request.getTokenHash());
        User user = getUser(email);
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        changePasswordRequestRepository.purgeToken(request.getTokenHash());
        SecurityContextHolder.getContext().setAuthentication(null);
    }
    
    private String generateRecoveryLink(String email) {
        return changePasswordRequestRepository.issueToken(email);
    }

    

}
