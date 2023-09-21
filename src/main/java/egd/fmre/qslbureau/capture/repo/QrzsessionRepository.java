package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import egd.fmre.qslbureau.capture.entity.Qrzsession;

public interface QrzsessionRepository extends JpaRepository<Qrzsession, Integer> {

    @Query("SELECT s FROM Qrzsession s WHERE (s.id = (SELECT MAX(s.id) FROM Qrzsession s)) and (s.error is null)")
    Qrzsession getLastSession();
}
