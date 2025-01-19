package com.example.immovision.entities.images;

import com.example.immovision.entities.property.Property;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "property_images")
public class PropertyImages {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id_property_images")
    private UUID id;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "image_url")
    private String image_url;

}
