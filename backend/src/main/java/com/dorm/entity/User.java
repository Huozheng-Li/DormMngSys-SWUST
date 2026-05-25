package com.dorm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "`user`")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 20)
    private String gender;

    @Column(length = 50)
    private String className;

    @Column(length = 50)
    private String major;

    @Column(length = 20)
    private String grade;

    @Column(length = 30)
    private String studentId;

    @Column(length = 30)
    private String employeeId;

    @Column(length = 50)
    private String department;

    public enum Role {
        STUDENT, COUNSELOR, DORM_MANAGER, ADMIN
    }
}
