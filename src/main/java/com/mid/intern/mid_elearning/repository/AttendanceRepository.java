package com.mid.intern.mid_elearning.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mid.intern.mid_elearning.model.Attendance;
import com.mid.intern.mid_elearning.model.User;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);
    List<Attendance> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    boolean existsByUserAndDate(User user, LocalDate date);
}
