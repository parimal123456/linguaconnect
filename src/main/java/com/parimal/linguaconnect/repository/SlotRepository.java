package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parimal.linguaconnect.entity.Slot;


public interface SlotRepository extends JpaRepository<Slot,Long>{

    Optional<Slot> findBySlot(String slot);

}
