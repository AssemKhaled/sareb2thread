package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Queries related to tc_users
 * @author fuinco
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    Optional<List<User>> findAllByClientIdAndDeleteDate(Long userId,String deleteDate);
    Optional<User> findByIdAndDeleteDate(Long id,String deleteDate);
    Optional<List<User>> findByVendorIdAndDeleteDate(Long userId,String deleteDate);
}
