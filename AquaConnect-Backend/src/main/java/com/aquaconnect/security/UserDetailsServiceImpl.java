package com.aquaconnect.security;

import com.aquaconnect.entity.Owner;
import com.aquaconnect.entity.User;
import com.aquaconnect.repository.OwnerRepository;
import com.aquaconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).get();

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        }

        if (ownerRepository.findByEmail(email).isPresent()) {
            Owner owner = ownerRepository.findByEmail(email).get();

            return new org.springframework.security.core.userdetails.User(
                    owner.getEmail(),
                    owner.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
            );
        }

        throw new UsernameNotFoundException("Account not found");
    }
}