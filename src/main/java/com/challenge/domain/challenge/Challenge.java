package com.challenge.domain.challenge;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.category.Category;
import com.challenge.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "title"})
)
public class Challenge extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Record> records = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(length = 500)
    private String content;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private Integer weeklyGoalCount;

    @Column(nullable = false)
    private Integer totalGoalCount;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

}
