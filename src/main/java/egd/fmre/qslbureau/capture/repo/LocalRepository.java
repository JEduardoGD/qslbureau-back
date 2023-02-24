package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, Integer> {
    
    public Local findById(int id);
    
}
