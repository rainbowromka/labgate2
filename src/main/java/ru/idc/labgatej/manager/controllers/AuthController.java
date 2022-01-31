package ru.idc.labgatej.manager.controllers;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.idc.labgatej.manager.model.ERole;
import ru.idc.labgatej.manager.model.Role;
import ru.idc.labgatej.manager.model.User;
import ru.idc.labgatej.manager.payload.request.LoginRequest;
import ru.idc.labgatej.manager.payload.request.SignupRequest;
import ru.idc.labgatej.manager.payload.response.JwtResponse;
import ru.idc.labgatej.manager.payload.response.MessageResponse;
import ru.idc.labgatej.manager.repo.RoleRepository;
import ru.idc.labgatej.manager.repo.UserRepository;
import ru.idc.labgatej.manager.security.jwt.JwtUtils;
import ru.idc.labgatej.manager.security.services.UserDetailsImpl;

/**
 * Контроллер авторизации.
 *
 * @author Роман Перминов.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Вход пользователя.
     *
     * @param loginRequest
     *        информация для входа в систему.
     * @return результат входа в систему.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
    {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails =
            (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        Cookie cookie = new Cookie("Token", jwt);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(false);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    /**
     * Зарегистрировать пользователя.
     *
     * @param signUpRequest
     *        информация для регистрации пользователя.
     * @return результат регистрации пользователя.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
        @Valid @RequestBody SignupRequest signUpRequest)
    {
        if (userRepository.existsByUsername((signUpRequest.getUsername())))
        {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail()))
        {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(
                    "Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException(
                            "Error: Role is not found."));
                    roles.add(adminRole);
                    break;
                case "mod":
                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        .orElseThrow(() -> new RuntimeException(
                            "Error: Role is not found"));
                    roles.add(modRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException(
                            "Error: Role is not found"));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
            "User registered successfully!"));
    }

    /**
     * Получить информацию о пользователе.
     * @param principal
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<?> userInfo(
        Principal principal)
    {
        return ResponseEntity.ok().body(principal);
    }
}
