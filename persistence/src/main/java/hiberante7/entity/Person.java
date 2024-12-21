package hiberante7.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "tb_person")
public class Person {
    @Id
    @GeneratedValue
    Long id;

    @NaturalId
    @NotNull
    String identification;

    @NotNull
    String name;

    public Person(String identification, String name) {
        this.identification = identification;
        this.name = name;
    }

    public Person() {
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Person person)) return false;

        return identification.equals(person.identification);
    }

    @Override
    public int hashCode() {
        return identification.hashCode();
    }
}
