package study.learningtestcode.study;

import org.springframework.data.jpa.repository.JpaRepository;

import study.learningtestcode.domain.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {

}
