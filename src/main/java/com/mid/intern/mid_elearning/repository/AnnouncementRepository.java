package com.mid.intern.mid_elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mid.intern.mid_elearning.model.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
