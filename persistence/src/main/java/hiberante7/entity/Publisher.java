package hiberante7.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Entity
@Table(name = "tb_publisher")
public class Publisher {
    @Id
    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Person person;

    @NaturalId
    @NotNull
    private String name;

    @OneToMany(mappedBy = Book_.PUBLISHER, fetch = FetchType.LAZY)
    private Set<Book> books;

    public Publisher(Person person, String name) {
        this.person = person;
        this.name = name;
    }

    public Publisher() {
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }


}
