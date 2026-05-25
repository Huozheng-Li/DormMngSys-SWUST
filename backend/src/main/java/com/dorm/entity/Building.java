package com.dorm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class Building extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    private Integer totalFloors;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Floor> floors;
}
