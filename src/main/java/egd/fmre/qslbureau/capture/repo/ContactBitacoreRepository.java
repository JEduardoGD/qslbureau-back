package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.ContactBitacore;

@Repository
public interface ContactBitacoreRepository extends JpaRepository<ContactBitacore, Integer> {

}
