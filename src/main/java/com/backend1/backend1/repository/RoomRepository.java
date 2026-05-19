package com.backend1.backend1.repository;

import com.backend1.backend1.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "  SELECT b.room.id FROM Booking b " +
            "  WHERE b.checkIn < :checkOut AND b.checkOut > :checkIn" +
            ")")
    List<Room> findAvailableByDates(@Param("checkIn") LocalDate checkIn,
                                    @Param("checkOut") LocalDate checkOut);
}
