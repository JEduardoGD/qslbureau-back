package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Qsl;

@Repository
public interface QslRepository extends JpaRepository<Qsl, Integer> {

}
