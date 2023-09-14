package egd.fmre.qslbureau.capture.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.component.JwtTokenUtil;
import egd.fmre.qslbureau.capture.dto.JwtRequest;
import egd.fmre.qslbureau.capture.dto.JwtResponse;
import egd.fmre.qslbureau.capture.dto.LocalDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.service.impl.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CapturerService capturerService;
    
    @Autowired
    private LocalService localService;

    @Autowired private ModelMapper modelMapper;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        log.info("{}", passwordEncoder.encode(authenticationRequest.getUsername()));
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        Capturer capturer = null;
        Set<Local> locals = null;

        if (userDetails != null) {
            if (passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
                capturer = capturerService.findByUsername(authenticationRequest.getUsername());
                locals = localService.findByCapturer(capturer);
            }
        }

        final String token = jwtTokenUtil.generateToken(userDetails);
        
        JwtResponse jwtResponse = new JwtResponse(token);
        if (capturer != null) {
            jwtResponse.setCapturerId(capturer.getId());
            jwtResponse.setCapturerName(capturer.getName());
            jwtResponse.setCapturerLastName(capturer.getLastName());
            jwtResponse.setCapturerUsername(capturer.getUsername());
            Set<LocalDto> LocalDtoSet = locals.stream().map(l -> modelMapper.map(l, LocalDto.class)).collect(Collectors.toSet());
            jwtResponse.setLocals(LocalDtoSet);
        }
        

        return ResponseEntity.ok(jwtResponse);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
