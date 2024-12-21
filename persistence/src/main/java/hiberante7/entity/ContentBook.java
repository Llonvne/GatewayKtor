package hiberante7.entity;

import hiberante7.type.BookType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_content_table")
public class ContentBook extends Book {
    @NotNull
    public String content;

    public ContentBook() {
    }

    public ContentBook(String isbn, String printing, String title, BookType bookType, String content, Publisher publisher) {
        super(isbn, printing, title, bookType, publisher);
        this.content = content;
    }
}
