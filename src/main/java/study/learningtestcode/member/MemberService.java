package study.learningtestcode.member;

import study.learningtestcode.domain.Member;

public interface MemberService {
	void validate(Long memberId) throws InvalidMemberException;

	Member findById(Long memberId) throws MemberNotFoundException;
}