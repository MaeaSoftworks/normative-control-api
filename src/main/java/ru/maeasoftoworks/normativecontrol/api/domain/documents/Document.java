package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "documents")
@NoArgsConstructor
@ToString
@Getter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Builder
    public Document(Student student, String fileName, boolean isReported, String comment) {
        this.student = student;
        this.fileName = fileName;
        this.isReported = isReported;
        this.comment = comment;
    }

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private Result result;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Student student;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_reported")
    @Setter
    private boolean isReported = false;

    @ElementCollection(targetClass = String.class)
    @Setter
    private Set<String> reportedMistakesIds = new HashSet<>();

    @Column(name = "status")
    @Setter
    private DocumentVerdict documentVerdict = DocumentVerdict.NOT_CHECKED;

    @Column(name = "comment")
    @Setter
    private String comment = "";

    @Column(name = "verification_date")
    @Setter
    private Date verificationDate = new Date();
}
