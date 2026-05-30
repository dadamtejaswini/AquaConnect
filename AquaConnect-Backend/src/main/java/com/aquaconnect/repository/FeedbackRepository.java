package com.aquaconnect.repository;

import com.aquaconnect.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Feedback> findByBranchOwnerIdOrderByCreatedAtDesc(Long ownerId);

    boolean existsByBookingIdAndUserId(Long bookingId, Long userId);

    List<Feedback> findByBranchIdOrderByCreatedAtDesc(Long branchId);

}