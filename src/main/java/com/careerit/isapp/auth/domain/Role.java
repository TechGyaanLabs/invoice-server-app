package com.careerit.isapp.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Table(name="roles")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
}
