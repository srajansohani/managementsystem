package com.Bank.managementSystem.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import com.Bank.managementSystem.repository.UserRepository;
import com.Bank.managementSystem.Security.AdminConfig;
import java.util.List;

public class CombinedUserDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
    private final UserRepository userRepository;

    @Autowired
    public CombinedUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminConfig adminConfig) {
        this.userRepository = userRepository;

        // Create in-memory admin user
        UserDetails admin = User.withUsername(adminConfig.getAdminUsername())
                .password(passwordEncoder.encode(adminConfig.getAdminPassword()))
                .roles("ADMIN")
                .build();
        this.inMemoryUserDetailsManager = new InMemoryUserDetailsManager(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if the username matches the configured admin username
        AdminConfig adminConfig = new AdminConfig();
        if (username.equals(adminConfig.getAdminUsername())) {
            // Create UserDetails for the admin user
            return new User(
                    adminConfig.getAdminUsername(),
                    adminConfig.getAdminPassword(),
                    true, true, true, true,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        UserDetails userDetails;
        com.Bank.managementSystem.entity.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new User(
                    user.getUsername(), user.getPassword(), true, true, true,
                    true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
