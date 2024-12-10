package study.learningtestcode.member;

import java.util.Optional;

import study.learningtestcode.domain.Member;

public interface MemberService {

	void validate(Long memberId) throws InvalidMemberException;

	Optional<Member> findById(Long memberId) throws MemberNotFoundException;
}