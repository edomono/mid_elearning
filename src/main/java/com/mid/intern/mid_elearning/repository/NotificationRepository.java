package com.mid.intern.mid_elearning.repository;

import com.mid.intern.mid_elearning.model.Notification;
import com.mid.intern.mid_elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByTimestampDesc(User recipient);

    List<Notification> findByRecipientAndIsReadFalseOrderByTimestampDesc(User recipient);

    long countByRecipientAndIsReadFalse(User recipient);
}
