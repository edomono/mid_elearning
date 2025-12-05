package com.mid.intern.mid_elearning.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AttendanceService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/attendance")
    public String myAttendance(Model model, @org.springframework.web.bind.annotation.RequestParam(value = "year", required = false) Integer year,
                               @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) Integer month,
                               jakarta.servlet.http.HttpServletRequest request) {
        User me = userService.getCurrentUser();
        if (me == null) return "redirect:/login";
        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        List<java.time.LocalDate> present = attendanceService.getPresentDatesForMonth(me, ym);
        model.addAttribute("owner", me);
        model.addAttribute("presentDays", present);
        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());
        model.addAttribute("canMark", true);
        model.addAttribute("isPrivilegedViewer", false);
        model.addAttribute("viewerPrefix", "/user");
        // previous and next month data for navigation
        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);
        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("prevMonth", prev.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        model.addAttribute("prevLink", "/user/attendance?year=" + prev.getYear() + "&month=" + prev.getMonthValue());
        model.addAttribute("nextLink", "/user/attendance?year=" + next.getYear() + "&month=" + next.getMonthValue());
        model.addAttribute("exportLink", "/user/attendance/export?year=" + ym.getYear() + "&month=" + ym.getMonthValue());
        return "user/attendance";
    }

    @PostMapping("/user/attendance/mark")
    public String markToday(Authentication authentication, Model model) {
        User me = userService.getCurrentUser();
        if (me == null) return "redirect:/login";
        LocalDate today = LocalDate.now();
        DayOfWeek dow = today.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            // Do not allow marking on weekends
            return "redirect:/user/attendance";
        }
        attendanceService.markAttendance(me, today, true);
        return "redirect:/user/attendance";
    }

    @GetMapping({"/admin/attendance/{id}", "/mentor/attendance/{id}"})
    public String viewAttendanceForUser(@PathVariable("id") Long id, Model model,
                                        @org.springframework.web.bind.annotation.RequestParam(value = "year", required = false) Integer year,
                                        @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) Integer month) {
        jakarta.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest();
        String prefix = request.getRequestURI().startsWith("/admin") ? "/admin" : "/mentor";
        User target = userService.getUserById(id).orElse(null);
        if (target == null) return "redirect:/";
        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        List<LocalDate> present = attendanceService.getPresentDatesForMonth(target, ym);
        model.addAttribute("owner", target);
        model.addAttribute("presentDays", present);
        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());
        model.addAttribute("canMark", false);
        model.addAttribute("isPrivilegedViewer", true);
        model.addAttribute("viewerPrefix", prefix);
        // navigation
        YearMonth prev = ym.minusMonths(1);
        YearMonth next = ym.plusMonths(1);
        model.addAttribute("prevYear", prev.getYear());
        model.addAttribute("prevMonth", prev.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        model.addAttribute("prevLink", prefix + "/attendance/" + id + "?year=" + prev.getYear() + "&month=" + prev.getMonthValue());
        model.addAttribute("nextLink", prefix + "/attendance/" + id + "?year=" + next.getYear() + "&month=" + next.getMonthValue());
        model.addAttribute("exportLink", prefix + "/attendance/" + id + "/export?year=" + ym.getYear() + "&month=" + ym.getMonthValue());
        double percent = attendanceService.getAttendancePercentageForMonth(target, ym);
        model.addAttribute("attendancePercentage", Math.round(percent * 100.0) / 100.0);
        return "user/attendance"; // reuse template
    }

    @GetMapping({"/admin/attendance/{id}/export", "/mentor/attendance/{id}/export"})
    public org.springframework.http.ResponseEntity<byte[]> exportCsv(@PathVariable("id") Long id,
                                                                     @org.springframework.web.bind.annotation.RequestParam(value = "year", required = false) Integer year,
                                                                     @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) Integer month) {
        User target = userService.getUserById(id).orElse(null);
        if (target == null) return org.springframework.http.ResponseEntity.notFound().build();
        // Role-based access: only ADMIN may use /admin/* exports, MENTOR or ADMIN may use /mentor/* exports
        jakarta.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        com.mid.intern.mid_elearning.model.User current = userService.getCurrentUser();
        if (current == null) return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        if (uri.startsWith("/admin") && (current.getRole() == null || !current.getRole().equalsIgnoreCase("ADMIN"))) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
        if (uri.startsWith("/mentor") && (current.getRole() == null || !(current.getRole().equalsIgnoreCase("MENTOR") || current.getRole().equalsIgnoreCase("ADMIN")))) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        java.util.Map<LocalDate, Boolean> map = attendanceService.getPresenceMapForMonth(target, ym);
        StringBuilder sb = new StringBuilder();
        sb.append("date,present\n");
        LocalDate d = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        while (!d.isAfter(end)) {
            java.time.DayOfWeek dow = d.getDayOfWeek();
            if (dow != java.time.DayOfWeek.SATURDAY && dow != java.time.DayOfWeek.SUNDAY) {
                sb.append(d.toString()).append(',').append(map.getOrDefault(d, false)).append('\n');
            }
            d = d.plusDays(1);
        }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = "attendance-" + target.getUsername() + "-" + ym.getYear() + "-" + ym.getMonthValue() + ".csv";
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=utf-8")
                .body(bytes);
    }

    // JSON endpoint used by modals to fetch attendance data
    @GetMapping("/api/attendance/{id}")
    public org.springframework.http.ResponseEntity<?> getAttendanceJson(@PathVariable("id") Long id,
                                                                       @org.springframework.web.bind.annotation.RequestParam(value = "year", required = false) Integer year,
                                                                       @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) Integer month) {
        User target = userService.getUserById(id).orElse(null);
        if (target == null) return org.springframework.http.ResponseEntity.notFound().build();

        // Role-based access: ADMIN and MENTOR can fetch any student's attendance; a USER can fetch only their own
        User current = userService.getCurrentUser();
        if (current == null) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        String role = current.getRole() != null ? current.getRole().toUpperCase() : "";
        boolean allowed = false;
        if ("ADMIN".equals(role) || "MENTOR".equals(role)) {
            allowed = true;
        } else if (current.getId() != null && current.getId().equals(target.getId())) {
            allowed = true; // owners can view their own attendance
        }
        if (!allowed) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }

        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        java.util.List<java.time.LocalDate> present = attendanceService.getPresentDatesForMonth(target, ym);
        double percent = attendanceService.getAttendancePercentageForMonth(target, ym);
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("ownerId", target.getId());
        resp.put("ownerUsername", target.getUsername());
        java.util.List<String> days = new java.util.ArrayList<>();
        for (java.time.LocalDate d : present) days.add(d.toString());
        resp.put("presentDays", days);
        resp.put("year", ym.getYear());
        resp.put("month", ym.getMonthValue());
        resp.put("attendancePercentage", Math.round(percent * 100.0) / 100.0);
        return org.springframework.http.ResponseEntity.ok(resp);
    }
}
