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
    Optional<List<User>> findAllByClientId(Long userId);

    @Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId ", nativeQuery = true)
    List<User> getActiveAndInactiveChildrenOfUser(@Param("userId") Long userId);
    @Query(value = "SELECT id FROM tc_users where vendor_id=:vendorId or id=:vendorId", nativeQuery = true)
    List<Long> getUsersByVendorId(@Param("vendorId") Long roleId);
}
