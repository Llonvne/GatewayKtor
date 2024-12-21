package hiberante7.query;

import hiberante7.entity.Book;
import hiberante7.entity.Publisher;
import org.hibernate.Session;
import org.hibernate.annotations.processing.HQL;
import org.hibernate.query.Page;

import java.util.List;

public interface Queries {

    Session session();

    @HQL("where title like :title order by title")
    List<Book> findBooksTitled(String title, Page page);

    @HQL("from Book")
    List<Book> selectAllBook();

    @HQL(" select b.title from Book b where b.id = :id")
    String selectTitleById(Long id);

    @HQL("from Publisher p where p.person.identification = :id")
    Publisher selectPublisherByPersonId(String id);
}
