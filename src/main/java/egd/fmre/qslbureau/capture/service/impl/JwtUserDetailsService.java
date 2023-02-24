package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.LocalService;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    LocalService localService;
    @Autowired
    CapturerService capturerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Capturer capturer = capturerService.findByUsername(username);
        if (capturer.getUsername().equals(username)) {
            return new User(capturer.getUsername(), capturer.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}