package com.backend1.backend1.repository;

import com.backend1.backend1.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByCustomerId(Long customerId);

    @Query("SELECT COUNT(b) FROM Booking b " +
           "WHERE b.room.id = :roomId " +
           "AND b.checkIn < :checkOut AND b.checkOut > :checkIn " +
           "AND (:excludeId IS NULL OR b.id <> :excludeId)")
    long countOverlap(@Param("roomId") Long roomId,
                      @Param("checkIn") LocalDate checkIn,
                      @Param("checkOut") LocalDate checkOut,
                      @Param("excludeId") Long excludeId);
}
