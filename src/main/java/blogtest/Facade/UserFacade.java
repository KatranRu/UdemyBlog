package blogtest.Facade;

import blogtest.DTO.UserDTO;
import blogtest.Model.Users;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public UserDTO userToUserDTO (Users users) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(users.getId());
        userDTO.setUsername(users.getUsername());
        userDTO.setFirstname(users.getName());
        userDTO.setLastname(users.getLastname());
        userDTO.setBiography(users.getBiography());
        return userDTO;
    }
}
