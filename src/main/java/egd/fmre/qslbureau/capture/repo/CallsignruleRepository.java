package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.CallsignRule;

@Repository
public interface CallsignruleRepository extends JpaRepository<CallsignRule, Integer> {
    public List<CallsignRule> findByCallsignTo(String callsignTo);
}
