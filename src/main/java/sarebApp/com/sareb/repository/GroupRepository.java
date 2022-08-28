package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.Group;

/**
 * @author Assem
 */
@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

}
