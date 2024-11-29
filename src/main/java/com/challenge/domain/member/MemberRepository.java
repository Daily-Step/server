package com.challenge.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findBySocialIdAndLoginType(Long socialId, LoginType loginType);

    boolean existsBySocialIdAndLoginType(Long socialId, LoginType loginType);

    boolean existsByNickname(String nickname);

}
