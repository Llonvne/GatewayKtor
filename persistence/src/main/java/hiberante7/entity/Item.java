package hiberante7.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_item")
public class Item {
    @Id
    @GeneratedValue
    Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns(foreignKey = @ForeignKey(name = "BookIsbnPrintingToItemFK"), value = {
            @JoinColumn(referencedColumnName = Book_.ISBN),
            @JoinColumn(referencedColumnName = Book_.PRINTING)
    })
    Book book;

    public Item() {
    }

    public Item(Book book) {
        this.book = book;
    }
}
