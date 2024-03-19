package jakarta.service;

import jakarta.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.repository.MemberRepository;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceTest {


    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception{
        //given - 주어짐
        Member member = new Member();
        member.setName("kim");

        //when - 실행시
        Long saveId = memberService.join(member);

        //then - 결과가 이렇게 나와야해
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
    }


    @Test
    public void 중복회원예외() throws Exception{
        //given - 주어짐
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when - 이떄
        memberService.join(member1);
        try{
            memberService.join(member2);
        }catch (IllegalStateException e){
            return;
        }




        //then - 이결과 나와야해
        fail("예외가 발생해야한다");

    }
}