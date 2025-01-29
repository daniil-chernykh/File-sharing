package ru.hamming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramField;
    private String docName;

    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
}
