package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import egd.fmre.qslbureau.capture.entity.Querylog;

public interface QuerylogRepository extends JpaRepository<Querylog, Integer> {

}
