package egd.fmre.qslbureau.capture.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.CapturerLocal;
import egd.fmre.qslbureau.capture.entity.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, Integer> {
    
    public Local findById(int id);
    
    public Set<Local>findByCapturerLocalsIn(Set<CapturerLocal> capturerLocals);
}
