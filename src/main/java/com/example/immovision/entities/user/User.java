package com.example.immovision.entities.user;

import com.example.immovision.entities.property.Property;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name="id_user")
    private UUID id;

    private String name;
    private String email;
    private String password;

    @Column(name = "google_id")
    private String googleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", referencedColumnName = "id_role")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Role roles;

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    private List<Property> ownedProperties;

    @OneToMany(mappedBy = "agent")
    @JsonBackReference
    private List<Property> managedProperties;


}