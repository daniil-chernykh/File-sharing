package ru.hamming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.configuration.JsonBinaryType;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Update event;

    public static RawDataBuilder builder() {
        return new RawDataBuilder();
    }

    public static class RawDataBuilder {
        private Long id;
        private Update event;

        public RawDataBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RawDataBuilder event(Update event) {
            this.event = event;
            return this;
        }

        public RawData build() {
            return new RawData(this.id, this.event);
        }

    }
}