package egd.fmre.qslbureau.capture.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.CapturerLocal;

@Repository
public interface CapturerLocalRepository extends JpaRepository<CapturerLocal, Integer> {
    public Set<CapturerLocal> findByCapturer(Capturer capturer);
}
