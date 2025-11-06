package com.mid.intern.mid_elearning.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mid.intern.mid_elearning.model.Discussion;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.DiscussionRepository;

@Service
public class ForumService {

    private final DiscussionRepository discussionRepository;

    public ForumService(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    // ✅ method tanpa parameter (cocok dengan controller kamu)
    public List<Discussion> getAllDiscussions() {
        return discussionRepository.findAllByOrderByCreatedAtAsc();
    }

    // ✅ save chat
    public void sendMessage(User sender, String message) {
        Discussion d = new Discussion();
        d.setSender(sender);
        d.setMessage(message);
        discussionRepository.save(d);
    }
}
