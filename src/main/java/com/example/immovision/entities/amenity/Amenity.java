package com.example.immovision.entities.amenity;
import com.example.immovision.entities.property.Property;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "amenities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "interior_amenities", joinColumns = @JoinColumn(name = "amenity_id"))
    @Column(name = "name")
    private List<String> interior = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "exterior_amenities", joinColumns = @JoinColumn(name = "amenity_id"))
    @Column(name = "name")
    private List<String> exterior = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "other_amenities", joinColumns = @JoinColumn(name = "amenity_id"))
    @Column(name = "name")
    private List<String> other = new ArrayList<>();

    @OneToOne(mappedBy = "amenities")
    @JsonBackReference
    private Property property;
}