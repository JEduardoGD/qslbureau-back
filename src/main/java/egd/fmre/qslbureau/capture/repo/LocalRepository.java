package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import egd.fmre.qslbureau.capture.entity.Local;

public interface LocalRepository extends JpaRepository<Local, Integer> {
    public Local findById(int id);
}
