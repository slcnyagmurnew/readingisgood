package com.example.readingisgood.unit;

import com.example.readingisgood.dao.BookDao;
import com.example.readingisgood.model.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class BookDaoTest {

    @Autowired
    private BookDao bookDao;

    // before tests run, add books into collection to use from another tests
    @BeforeEach
    public void before() {
        Book book =  new Book("testBook", 30.99, 50);
        Book book1 =  new Book("willUpdateBook", 40.99, 50);
        Book book2 =  new Book("testBook2", 50.99, 50);

        bookDao.save(book);
        bookDao.save(book1);
        bookDao.save(book2);
    }

    @Test
    void getTest(){
        Book book = new Book("testBook", 30.99, 50);
        bookDao.get(book.getName()).ifPresentOrElse(actualBook -> assertThat(actualBook).isEqualTo(book), () -> assertThat(book).isEqualTo(null));
    }

    @Test
    void saveTest() {
        Book book = new Book("savedBook", 20.00, 30);
        bookDao.save(book);
        bookDao.get(book.getName()).ifPresentOrElse(addedBook -> assertThat(addedBook).isEqualTo(book), () -> assertThat(book).isEqualTo(null));
    }

    @Test
    void updateTest() {
        bookDao.get("willUpdateBook").ifPresent(book -> {
            book.setStock(-5);
            bookDao.update(book);
        });
        bookDao.get("willUpdateBook").ifPresent(book -> {
            assertThat(book.getStock()).isEqualTo(45);
        });
    }

    @Test
    void deleteAllTest() {
        // can not do this test because the data will be lost
    }

    // clear collection after all the tests run
    @AfterEach
    public void clean() {
        bookDao.deleteAll();
    }
}
