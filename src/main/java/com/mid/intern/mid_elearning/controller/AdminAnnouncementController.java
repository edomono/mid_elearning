package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin/announcement")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    public AdminAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping("/add")
    public String addAnnouncement(@RequestParam("content") String content, Principal principal) {
        if (content != null && !content.trim().isEmpty()) {
            Announcement a = new Announcement();
            a.setContent(content.trim());
            a.setUsername(principal.getName());
            a.setRole("ADMIN");
            announcementService.saveAnnouncement(a);
        }
        // This assumes the admin is on a course details page and we want to redirect back there.
        // A more robust solution might involve passing the redirect URL as a parameter.
        return "redirect:/admin/dashboard"; 
    }

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        if (announcementService.existsById(id)) {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
