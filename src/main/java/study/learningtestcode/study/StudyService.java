package study.learningtestcode.study;

import study.learningtestcode.domain.Member;
import study.learningtestcode.domain.Study;
import study.learningtestcode.member.MemberService;
import study.learningtestcode.study.StudyRepository;

public class StudyService {

	private final MemberService memberService;

	private final StudyRepository repository;

	public StudyService(MemberService memberService, StudyRepository repository) {
		assert memberService != null;
		assert repository != null;
		this.memberService = memberService;
		this.repository = repository;
	}

	public Study createNewStudy(Long memberId, Study study) {
		Member member = memberService.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member doesn't exist for id: '" + memberId + "'"));
		study.setOwner(member);
		Study newStudy = repository.save(study);
		// 알람이 가는 서비스가 있다고 가정
		memberService.notify(newStudy);
		memberService.notify(member);
		return newStudy;
	}

}
