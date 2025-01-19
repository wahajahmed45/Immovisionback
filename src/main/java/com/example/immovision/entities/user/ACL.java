package com.example.immovision.entities.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "acls")
public class ACL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "acls")
    private List<Role> roles;
}