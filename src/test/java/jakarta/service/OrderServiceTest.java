package jakarta.service;

import jakarta.domain.Address;
import jakarta.domain.Member;
import jakarta.domain.Order;
import jakarta.domain.OrderStatus;
import jakarta.domain.item.Book;
import jakarta.domain.item.Item;
import jakarta.exception.NotEnoughStockException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {
    @PersistenceContext
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given - 주어짐
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount  = 2;

        //when - 이떄
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then - 검증
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다");
        assertEquals(10000*2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다");
        assertEquals( 8, book.getStockQuantity(),"주문 수량만큼 재고가 줄어야한다");

    }


    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given - 주어짐
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);


        int orderCount = 11;
        //when - 이떄
        //여기서 try-catch로 잡는다
        try{
            orderService.order(member.getId(), item.getId(), orderCount);
        }catch (NotEnoughStockException e){
            return;
        }

        //then - 이결과 나와야해
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    @Test
    public void 주문취소() throws Exception{
        //given - 주어짐
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //when - 이떄
        orderService.cancelOrder(orderId);

        //then - 이결과 나와야해
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다");
        assertEquals(10, item.getStockQuantity(), "주문 취소된 상품은 그만큼 재고가 증가해야한다");
    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가","123-123"));
        em.persist(member);
        return member;
    }


}