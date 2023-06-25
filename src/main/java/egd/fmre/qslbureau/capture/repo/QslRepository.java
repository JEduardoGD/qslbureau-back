package egd.fmre.qslbureau.capture.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;

@Repository
public interface QslRepository extends JpaRepository<Qsl, Integer> {

    Set<Qsl> findBySlot(Slot slot);

    @Query(value = "SELECT Q From Qsl Q INNER JOIN Q.slot s INNER JOIN s.local l WHERE l = :local")
    Set<Qsl> findByLocal(@Param("local") Local slot);

    @Query(value = "SELECT Q From Qsl Q INNER JOIN Q.slot s INNER JOIN s.local l WHERE l = :local")
    List<Qsl> findByPaggeable(@Param("local") Local slot, Pageable pageable);

}
