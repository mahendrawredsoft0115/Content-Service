package com.project.content.entity;

import com.project.content.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


//    @Column(filename = file_name )
    private String filename;
    private String fileType;
    private String url;
    private Long creatorId; //  Who uploaded this post

    @Enumerated(EnumType.STRING)
    private Visibility visibility; //  public or private


    private LocalDateTime uploadedAt;
}
