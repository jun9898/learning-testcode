package study.learningtestcode.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;
import study.learningtestcode.domain.Member;
import study.learningtestcode.domain.Study;
import study.learningtestcode.member.MemberService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
class StudyServiceWithRepositoryTest {

	@Mock
	MemberService memberService;

	@Autowired
	StudyRepository studyRepository;

	@Container
	static GenericContainer<?> postgreSQLContainer = new GenericContainer<>("postgres")
		.withExposedPorts(5432)
		.withEnv("POSTGRES_DB", "studytest")
		.withEnv("POSTGRES_USER", "studytest")
		.withEnv("POSTGRES_PASSWORD", "studytest")
		.waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1));

	@BeforeAll
	static void beforeAll() {
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
		postgreSQLContainer.followOutput(logConsumer);
	}

	@BeforeEach
	void beforeEach() {
		studyRepository.deleteAll();
	}

	@Test
	void createNewStudy() {
		// Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);

		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");

		Study study = new Study(10, "테스트");

		given(memberService.findById(1L)).willReturn(Optional.of(member));

		// When
		studyService.createNewStudy(1L, study);

		// Then
		assertEquals(1L, study.getOwnerId());
		then(memberService).should(times(1)).notify(study);
		then(memberService).shouldHaveNoMoreInteractions();
	}

	@DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
	@Test
	void openStudy() {
		// Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		Study study = new Study(10, "더 자바, 테스트");
		assertNull(study.getOpenedDateTime());

		// When
		studyService.openStudy(study);

		// Then
		assertEquals(StudyStatus.OPENED, study.getStatus());
		assertNotNull(study.getOpenedDateTime());
		then(memberService).should().notify(study);
	}

}
