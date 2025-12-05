package com.mid.intern.mid_elearning.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AttendanceService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/admin/participant")
public class AdminAttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}/attendance")
    public String getAttendanceDetails(@PathVariable Long id, 
                                       @RequestParam Optional<Integer> month,
                                       @RequestParam Optional<Integer> year,
                                       Model model) {
        User participant = userService.getUserById(id).orElse(null);
        if (participant == null) {
            return "redirect:/admin/participants";
        }

        YearMonth ym = YearMonth.now();
        if (month.isPresent() && year.isPresent()) {
            ym = YearMonth.of(year.get(), month.get());
        }

        List<LocalDate> present = attendanceService.getPresentDatesForMonth(participant, ym);
        logger.debug("Admin viewing attendance for participant ID: {}, month: {}, year: {}. Present days: {}", id, ym.getMonthValue(), ym.getYear(), present);
        model.addAttribute("owner", participant);
        model.addAttribute("presentDays", present);
        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());
        model.addAttribute("canMark", false);
        model.addAttribute("isPrivilegedViewer", true);
        model.addAttribute("viewerPrefix", "/admin");
        // navigation
        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);
        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("prevMonth", prev.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        model.addAttribute("prevLink", "/admin/participant/" + id + "/attendance?year=" + prev.getYear() + "&month=" + prev.getMonthValue());
        model.addAttribute("nextLink", "/admin/participant/" + id + "/attendance?year=" + next.getYear() + "&month=" + next.getMonthValue());
        model.addAttribute("exportLink", "/admin/participant/" + id + "/attendance/export?year=" + ym.getYear() + "&month=" + ym.getMonthValue());
        double percent = attendanceService.getAttendancePercentageForMonth(participant, ym);
        model.addAttribute("attendancePercentage", Math.round(percent * 100.0) / 100.0);
        return "user/attendance"; // reuse the student attendance template for admin view
    }
}