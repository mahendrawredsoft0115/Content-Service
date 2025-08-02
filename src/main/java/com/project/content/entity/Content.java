//package com.project.content.entity;
//
//import com.project.content.enums.ContentType;
//import com.project.content.enums.FileType;
//import com.project.content.enums.Visibility;
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
///**
// * Entity representing content uploaded by a creator.
// */
//@Entity
//@Table(name = "contents")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Content {
//
//    @Id
//    @GeneratedValue
//    private UUID id;
//
//    @Column(nullable = false)
//    private Long creatorId;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(length = 1000)
//    private String description;
//
//    @Column(nullable = false)
//    private String filename;
//
//    @Column(nullable = false)
//    private String url;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Visibility visibility;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private FileType fileType;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ContentType contentType;
//
//    @Column(nullable = false)
//    private BigDecimal price;
//
//    @Column(nullable = false)
//    private LocalDateTime uploadedAt;
//}



package com.project.content.entity;

import com.project.content.enums.Visibility;
import com.project.content.enums.FileType;
import com.project.content.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing content uploaded by creators.
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // ✅ New Fields for Moderation
    @Column
    private Boolean moderated;

    @Column(length = 500)
    private String moderationReason;
}
