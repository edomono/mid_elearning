package com.mid.intern.mid_elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mid.intern.mid_elearning.model.Discussion;
import com.mid.intern.mid_elearning.model.User;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    List<Discussion> findAllByOrderByCreatedAtAsc();

    List<Discussion> findBySender(User sender);
}
