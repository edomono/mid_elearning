package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.repository.AnnouncementRepository;

@Service
public class AnnouncementService {

    private final AnnouncementRepository repo;

    public AnnouncementService(AnnouncementRepository repo) {
        this.repo = repo;
    }

    public List<Announcement> getAllAnnouncementsSorted() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Announcement> getAnnouncementById(Long id) {
        return repo.findById(id);
    }

    public void saveAnnouncement(Announcement a) {
        repo.save(a);
    }

    public void deleteAnnouncement(Long id) {
        repo.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repo.existsById(id);
    }
}
