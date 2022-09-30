package blogtest.Service;

import blogtest.Model.Users;
import blogtest.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findUsersByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username: " + username));
        return build(users);
    }

    public Users loadUserById(Long id) {
        return  userRepository.findUsersById(id).orElse(null);
    }

    public static Users build(Users users) {
        List<GrantedAuthority> authorities = users.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        return new Users(
                users.getId(),
                users.getUsername(),
                users.getEmail(),
                users.getPassword(),
                authorities);
    }
}
