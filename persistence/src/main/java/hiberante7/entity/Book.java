package hiberante7.entity;

import hiberante7.type.BookType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;

import java.time.ZonedDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_book_basic")
@SecondaryTable(name = "tb_book_full", pkJoinColumns = @PrimaryKeyJoinColumn(name = "bookId"))
public class Book {
    @Id
    @GeneratedValue
    public Long id;

    @NaturalId
    @NotNull
    @Column(table = "tb_book_basic")
    public String isbn;

    @NaturalId
    @NotNull
    @Column(table = "tb_book_basic")
    public String printing;

    @NotNull
    @Column(table = "tb_book_basic")
    public String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    Publisher publisher;

    @NotNull
    @Column(table = "tb_book_full")
    public BookType bookType;

    @NotNull
    @Column(table = "tb_book_full")
    public ZonedDateTime zonedDateTime;

    public Book() {
    }

    public Book(String isbn, String printing, String title, BookType bookType, Publisher publisher) {
        this.isbn = isbn;
        this.printing = printing;
        this.title = title;
        this.bookType = bookType;
        this.publisher = publisher;
        this.zonedDateTime = ZonedDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;

        return isbn.equals(book.isbn) && printing.equals(book.printing);
    }

    @Override
    public int hashCode() {
        int result = isbn.hashCode();
        result = 31 * result + printing.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", printing='" + printing + '\'' +
                ", title='" + title + '\'' +
                ", publisher=" + publisher +
                ", bookType=" + bookType +
                ", zonedDateTime=" + zonedDateTime +
                '}';
    }
}
