package sarebApp.com.sareb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.entities.ThreadProcessedData;

/**
 * @author Assem
 */
@Repository
public interface ThreadProcessedDataRepository extends JpaRepository<ThreadProcessedData,Long> {

}
