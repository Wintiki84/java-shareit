package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "description", length = 512, nullable = false)
    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User requestor;

    @Column(name = "date_created", nullable = false)
    @NotNull
    private LocalDateTime created;

}