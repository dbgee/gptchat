package com.kk.gptchat.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 4096)
    private String question;
    @Column(length = 4096)
    private String answer;
    @Column(length = 128)
    private String openid;
    @Column(length = 64)
    private String uuid;
}
