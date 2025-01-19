package com.example.immovision.entities.user;

import com.example.immovision.entities.property.Property;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @JsonManagedReference  // Change this from JsonBackReference
    private List<Property> ownedProperties;

    @OneToMany(mappedBy = "agent")
    @JsonManagedReference  // Change this from JsonBackReference
    private List<Property> managedProperties;


}