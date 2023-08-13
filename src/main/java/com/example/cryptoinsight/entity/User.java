package com.example.cryptoinsight.entity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name ="user")
@Data
public class User {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userId;


    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    @Column
    private String password;


}
