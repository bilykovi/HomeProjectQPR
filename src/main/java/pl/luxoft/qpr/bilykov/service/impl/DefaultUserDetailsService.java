package pl.luxoft.qpr.bilykov.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;
import pl.luxoft.qpr.bilykov.repository.UserRepository;
import pl.luxoft.qpr.bilykov.model.User;
import pl.luxoft.qpr.bilykov.repository.UserRepository;

@Log
@Service
public class DefaultUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User user = repository.findByEmail(username);
        
        if (user == null) {
            log.fine(String.format("User with email [%s] was not found", username));
            throw new UsernameNotFoundException(username);
        }
        
        // TODO add granted role
        List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(),  true, true,
                true, true, gas);
    }
}
