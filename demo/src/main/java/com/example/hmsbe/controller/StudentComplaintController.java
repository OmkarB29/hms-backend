package com.example.hmsbe.controller;

import com.example.hmsbe.model.Complaint;
import com.example.hmsbe.repo.ComplaintRepository;
import com.example.hmsbe.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.hmsbe.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/complaints")
@CrossOrigin(origins = {"http://localhost:3000", "http://hmsclg.netlify.app", "https://hmsclg.netlify.app"})
public class StudentComplaintController {

    private static final Logger log = LoggerFactory.getLogger(StudentComplaintController.class);

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 Add new complaint
    @PostMapping
    public Complaint submitComplaint(
            @RequestHeader(value = "x-test-user", required = false) String xTestUser,
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody Complaint complaint) {
        // Default status when a student submits a complaint
        complaint.setStatus("Pending");

        // Prefer token-derived username to set student name
        String username = null;
        if (xTestUser != null && !xTestUser.isBlank()) username = xTestUser;
        else if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception ex) {
                log.warn("Failed to extract username from token for complaint", ex);
            }
        }

        if (username != null) {
            studentRepository.findByUsername(username).ifPresent(s ->
                    complaint.setStudentName(s.getName() != null && !s.getName().isBlank() ? s.getName() : s.getUsername())
            );
        } else {
            // Fallback: normalize if frontend provided username
            if (complaint.getStudentName() != null && !complaint.getStudentName().isBlank()) {
                studentRepository.findByUsername(complaint.getStudentName()).ifPresent(s ->
                        complaint.setStudentName(s.getName() != null && !s.getName().isBlank() ? s.getName() : s.getUsername())
                );
            }
        }

        return complaintRepository.save(complaint);
    }

    // 🔹 Get all complaints (for that student or admin — you can filter later)
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
}


