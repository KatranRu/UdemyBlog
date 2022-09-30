package blogtest.Service;

import blogtest.DTO.UserDTO;
import blogtest.Exceptions.UserExistException;
import blogtest.Model.Users;
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

    public Users getUserById(Long id) {
        return userRepository.findUsersById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with userId: " + id));
    }

    public Users createUser(SignupRequest userIn) {
        Users users = new Users();
        users.setEmail(userIn.getEmail());
        users.setName(userIn.getFirstname());
        users.setLastname(userIn.getLastname());
        users.setUsername(userIn.getUsername());
        users.setPassword(passwordEncoder.encode(userIn.getPassword()));
        users.getRole().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving Users");
            return userRepository.save(users);
        } catch (Exception ex) {
            LOG.error("Error during registration. {}", ex.getMessage());
            throw new UserExistException("The users " + users.getUsername() + " already. Please check credentials");
        }
    }

    public Users updateUser(UserDTO userDTO, Principal principal) {
        Users users = getUserByPrincipal(principal);
        users.setName(userDTO.getFirstname());
        users.setLastname(userDTO.getLastname());
        users.setBiography(userDTO.getBiography());

        return userRepository.save(users);
    }

    public Users getCurrentUser(Principal principal){
        return getUserByPrincipal(principal);
    }

    private Users getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUsersByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }
}
