package com.example.aboutme.repository;

import com.example.aboutme.domain.Alarm;
import com.example.aboutme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByMember(Member member);

}

