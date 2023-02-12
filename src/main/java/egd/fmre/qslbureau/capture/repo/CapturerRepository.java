package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Capturer;

@Repository
public interface CapturerRepository extends JpaRepository<Capturer, Integer>{

}
