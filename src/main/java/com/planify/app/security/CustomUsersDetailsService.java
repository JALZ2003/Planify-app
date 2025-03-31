package com.planify.app.security;

import com.planify.app.models.User; // Asegúrate de importar tu modelo de usuario
import com.planify.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.ArrayList;

@Service
public class CustomUsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuario = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList() // Puedes agregar roles aquí si los manejas
        );
    }
}
