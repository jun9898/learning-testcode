package study.learningtestcode.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import study.learningtestcode.domain.Member;
import study.learningtestcode.member.InvalidMemberException;
import study.learningtestcode.member.MemberNotFoundException;
import study.learningtestcode.member.MemberService;

// 해당 옵션을 주면 Mock으로 가짜 객체가 생성된다.
@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

	// 여러 테스트에서 사용하려면 해당 방식이 맞지만 일부 테스트에서만 어노테이션으로 사용하고 싶다면 다음과 같이 작성하면 된다.
	// @Mock MemberService memberService;
	// @Mock StudyRepository studyRepository;

	@Test
	// 어노테이션 + 매개변수로 넘김으로써 Mocking 할 수 있다.
	void createStudyService(
		@Mock MemberService memberService,
		@Mock StudyRepository studyRepository
	) {
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
	}

}