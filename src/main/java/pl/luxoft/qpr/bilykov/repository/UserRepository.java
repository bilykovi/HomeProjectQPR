package pl.luxoft.qpr.bilykov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.homebudget.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

}
