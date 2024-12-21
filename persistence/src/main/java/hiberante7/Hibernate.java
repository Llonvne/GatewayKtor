package hiberante7;

import hiberante7.entity.*;
import hiberante7.query.Queries_;
import hiberante7.type.BookType;
import org.hibernate.Session;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

public class Hibernate {

    private static final HibernatePersistenceConfiguration configuration = new HibernatePersistenceConfiguration("Bookshelf")
            .managedClass(Book.class)
            .managedClass(ContentBook.class)
            .managedClass(Publisher.class)
            .managedClass(Person.class)
            .managedClass(Item.class)
            .property("hibernate.agroal.maxSize", "20")
            .showSql(true, true, true);

    private static void persist(Session session) {
        var person = new Person("test-id", "Llonvne");
        session.persist(person);
        var publisher = new Publisher(person, "Llonvne Publisher");
        session.persist(publisher);
        Book book = new Book("test-isbn", "0", "test-title", BookType.UNSPECIFIED, publisher);
        session.persist(book);
        session.persist(new ContentBook("test-isbn2", "0", "test-title2", BookType.UNSPECIFIED, "Content!", publisher));
        session.persist(new Item(book));
    }

    public static void run(String[] args) {
        try (var sessionFactory = configuration.createEntityManagerFactory()) {
            sessionFactory.getSchemaManager().exportMappedObjects(true);

            sessionFactory.getSchemaManager().truncateMappedObjects();

            sessionFactory.inTransaction(Hibernate::persist);

            sessionFactory.inTransaction(session -> {
                var query = new Queries_(session);
                var publisher = query.selectPublisherByPersonId("test-id");

                System.out.println("### BEGIN");

                System.out.println(publisher.getName());
            });
        }

    }
}
