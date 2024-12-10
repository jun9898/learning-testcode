package study.learningtestcode.member;

import java.util.Optional;

import study.learningtestcode.domain.Member;
import study.learningtestcode.domain.Study;

public interface MemberService {

	void validate(Long memberId) throws InvalidMemberException;

	Optional<Member> findById(Long memberId) throws MemberNotFoundException;

	void notify(Study newStudy);
	void notify(Member member);
}