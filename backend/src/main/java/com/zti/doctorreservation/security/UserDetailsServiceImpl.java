package com.zti.doctorreservation.security;

import com.zti.doctorreservation.model.User;
import com.zti.doctorreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacja {@link UserDetailsService} używana przez Spring Security
 * do ładowania danych użytkownika podczas uwierzytelniania.
 * Mapuje rolę użytkownika z bazy na autorytet Spring Security w formacie {@code ROLE_*}.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Ładuje dane użytkownika na podstawie nazwy użytkownika.
     * Rola z bazy danych jest mapowana na autorytet {@code ROLE_PATIENT},
     * {@code ROLE_DOCTOR} lub {@code ROLE_ADMIN}.
     *
     * @param username nazwa użytkownika
     * @return obiekt {@link UserDetails} z danymi i uprawnieniami użytkownika
     * @throws UsernameNotFoundException gdy użytkownik o podanej nazwie nie istnieje
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
