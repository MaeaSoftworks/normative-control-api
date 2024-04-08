package ru.maeasoftoworks.normativecontrol.api.domain.universities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "academic_groups")
@NoArgsConstructor
@ToString
@Getter
public class AcademicGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public AcademicGroup(University university, String name) {
        this.university = university;
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @Column(name = "name", unique = true)
    @Setter
    private String name;
}