package UserService.userService.repositories;

import UserService.userService.entites.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByUsernameShouldReturnUser() {
        User user = new User("freddan");
        userRepository.save(user);

        User response = userRepository.findUserByUsername("freddan");

        assertEquals("freddan", response.getUsername(), "ERROR: Usernames was not identical");
        assertEquals(user, response, "ERROR: was not identical");
    }
}