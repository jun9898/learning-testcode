package study.learningtestcode.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.bytebuddy.implementation.bytecode.Throw;

import study.learningtestcode.domain.Member;
import study.learningtestcode.domain.Study;
import study.learningtestcode.member.InvalidMemberException;
import study.learningtestcode.member.MemberNotFoundException;
import study.learningtestcode.member.MemberService;

// 해당 옵션을 주면 Mock으로 가짜 객체가 생성된다.
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyServiceTest {

	// 여러 테스트에서 사용하려면 해당 방식이 맞지만 일부 테스트에서만 어노테이션으로 사용하고 싶다면 다음과 같이 작성하면 된다.
	@Mock MemberService memberService;
	@Mock StudyRepository studyRepository;

	Member member;
	Study study;

	@BeforeEach
	void set_up() throws Exception {
		member = Member.builder()
			.id(1L)
			.email("test@test.com")
			.build();
		study = new Study(10);
	}

	@Test
	// 어노테이션 + 매개변수로 넘김으로써 Mocking 할 수 있다.
	void mocking_test(
		// @Mock MemberService memberService,
		// @Mock StudyRepository studyRepository
	) {
		StudyService studyService = new StudyService(memberService, studyRepository);
	}

	@Test
	void any_long_test() {
		// memberService의 findById가 호출이 되면 (when)
		// Optional에 감쌓인 member를 return 하라
		when(memberService.findById(anyLong())).thenReturn(Optional.of(member));

		Member byId1 = memberService.findById(1L)
			.orElseThrow(MemberNotFoundException::new);
		Member byId2 = memberService.findById(2L)
			.orElseThrow(MemberNotFoundException::new);

		assertEquals(member, byId1);
		assertEquals(member, byId2);
	}

	@Test
	void do_throw_test() {
		// IllegalArgumentException을 던져라(doThrow)
		// memberService의 vaildate 함수에 1L이 인자로 들어왔을때(when)
		doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
		assertThrows(IllegalArgumentException.class, () -> {
			memberService.validate(1L);
		});
		memberService.validate(2L);
	}

	@Test
	void what_the_test() {
		// 첫번째 호출 -> 정상, 두번째 호출 -> Exception 발생, 세번째 호출 -> empty
		when(memberService.findById(any()))
			.thenReturn(Optional.of(member))
			.thenThrow(MemberNotFoundException.class)
			.thenReturn(Optional.empty());
		assertEquals(member, memberService.findById(1L).orElseThrow(MemberNotFoundException::new));
		assertThrows(MemberNotFoundException.class, () -> {
			memberService.findById(2L).orElseThrow(MemberNotFoundException::new);
		});
		assertEquals(Optional.empty(), memberService.findById(3L));
	}

	@Test
	void create_new_study() {
		// 1. Mocking - memberService, studyRepository (clear)
		// 2. Stubbing - memberService.findById -> member return, studyRepository.save -> study return (clear)
		// 3. validation - assertEquals member, study (clear)
		StudyService studyService = new StudyService(memberService, studyRepository);

		when(memberService.findById(1L)).thenReturn(Optional.of(member));
		when(studyRepository.save(study)).thenReturn(study);

		Member byId = memberService.findById(1L)
			.orElseThrow(MemberNotFoundException::new);
		Study newStudy = studyService.createNewStudy(byId.getId(), study);

		Study save = studyRepository.save(newStudy);

		assertNotNull(save.getOwner());
		assertEquals(save.getOwner(), byId);
	}

	@Test
	void mock_verify_test() {
		StudyService studyService = new StudyService(memberService, studyRepository);

		when(memberService.findById(1L)).thenReturn(Optional.of(member));
		when(studyRepository.save(study)).thenReturn(study);

		Member byId = memberService.findById(1L)
			.orElseThrow(MemberNotFoundException::new);
		studyService.createNewStudy(byId.getId(), study);

		// memberService의 study 객체를 매개변수로 하는 notify 함수가 한번은 실행되어야 한다.
		verify(memberService, times(1)).notify(study);
		verify(memberService, times(1)).notify(member);
		// memberService의 validate 함수는 실행되어서는 안된다.
		verify(memberService, never()).validate(any());

		// study를 먼저 호출하고 member를 다음에 호출하는가
		InOrder inOrder = inOrder(memberService);
		inOrder.verify(memberService).notify(study);

		// memberService.notify(study)가 호출된 후에는 아무것도 호출되어선 안된다.
		// verifyNoInteractions(memberService);

		inOrder.verify(memberService).notify(member);
	}
}