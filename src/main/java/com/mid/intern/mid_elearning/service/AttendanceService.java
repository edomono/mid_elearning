package com.mid.intern.mid_elearning.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import com.mid.intern.mid_elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mid.intern.mid_elearning.model.Attendance;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.AttendanceRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public Attendance markAttendance(User user, LocalDate date, boolean present) {
        if (attendanceRepository.existsByUserAndDate(user, date)) {
            // do not duplicate - return existing
            return attendanceRepository.findByUserAndDateBetween(user, date, date).stream().findFirst().orElse(null);
        }
        User managedUser = userRepository.findById(user.getId()).orElse(null);
        Attendance a = new Attendance(managedUser, date, present);
        return attendanceRepository.save(a);
    }

    public List<LocalDate> getPresentDatesForMonth(User user, YearMonth ym) {
        User managedUser = userRepository.findById(user.getId()).orElse(null);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return attendanceRepository.findByUserAndDateBetween(managedUser, start, end)
                .stream()
                .filter(Attendance::getPresent)
                .map(Attendance::getDate)
                .collect(Collectors.toList());
    }

    public java.util.Map<LocalDate, Boolean> getPresenceMapForMonth(User user, YearMonth ym) {
        User managedUser = userRepository.findById(user.getId()).orElse(null);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return attendanceRepository.findByUserAndDateBetween(managedUser, start, end)
                .stream()
                .collect(Collectors.toMap(Attendance::getDate, Attendance::getPresent));
    }

    public double getAttendancePercentageForMonth(User user, YearMonth ym) {
        java.util.Map<LocalDate, Boolean> map = getPresenceMapForMonth(user, ym);
        int present = 0;
        int workingDays = 0;
        LocalDate d = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        while (!d.isAfter(end)) {
            java.time.DayOfWeek dow = d.getDayOfWeek();
            if (dow != java.time.DayOfWeek.SATURDAY && dow != java.time.DayOfWeek.SUNDAY) {
                workingDays++;
                if (map.getOrDefault(d, false)) present++;
            }
            d = d.plusDays(1);
        }
        if (workingDays == 0) return 0.0;
        return (present * 100.0) / workingDays;
    }

    public List<Attendance> getAllForUser(User user) {
        User managedUser = userRepository.findById(user.getId()).orElse(null);
        return attendanceRepository.findByUser(managedUser);
    }
}
