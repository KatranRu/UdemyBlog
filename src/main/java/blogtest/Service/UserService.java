package blogtest.Service;

import blogtest.DTO.UserDTO;
import blogtest.Exceptions.UserExistException;
import blogtest.Model.User;
import blogtest.Model.enums.ERole;
import blogtest.Payload.Request.SignupRequest;
import blogtest.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignupRequest userIn) {
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setName(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRole().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving User");
            return userRepository.save(user);
        } catch (Exception ex) {
            LOG.error("Error during registration. {}", ex.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already. Please check credentials");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setName(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setBiography(userDTO.getBiography());

        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal){
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }
}
