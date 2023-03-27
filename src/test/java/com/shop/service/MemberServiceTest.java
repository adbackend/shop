package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *  given : 무언가 주어졌을 때
 *  when  : 이것을 실행했을 때
 *  then  : 결과가 이게 나와야된다
 * */
@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){

        MemberFormDto memberFormDto = new MemberFormDto();

        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 강남구 삼성동");
        memberFormDto.setPassword("1234");

        return Member.createMember(memberFormDto, passwordEncoder);

    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){

        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 테스트")
    public void saveDuplicateMemberTest(){

        //given
        Member member1 = createMember();
        Member member2 = createMember();

        memberService.saveMember(member1);

        //when
        //assertThrows : 원하는 예외확인
        Throwable e = assertThrows(IllegalStateException.class, ()-> {
           memberService.saveMember(member2);
        });

        //then
        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
}
