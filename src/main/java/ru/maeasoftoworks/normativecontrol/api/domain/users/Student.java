package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

@Entity(name = "students")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Student extends User {

    @Builder
    public Student(Normocontroller normocontroller, String email, String password, boolean isVerified,
                   String firstName, String middleName, String lastName, University university, AcademicGroup academicGroup,
                   int documentsLimit) {
        this.normocontroller = normocontroller;
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.firstName = firstName;
        super.middleName = middleName;
        super.lastName = lastName;
        super.university = university;
        this.academicGroup = academicGroup;
        this.documentsLimit = documentsLimit;
        this.role = Role.STUDENT;
    }

    @ManyToOne
    @JoinColumn(name = "normocontroller")
    private Normocontroller normocontroller;

    @ManyToOne
    @JoinColumn(name = "academic_group")
    private AcademicGroup academicGroup;

    @Column(name = "documents_limit")
    private int documentsLimit;

    private Role role;

    @Override
    public Role getRole() {
        return this.role;
    }
}