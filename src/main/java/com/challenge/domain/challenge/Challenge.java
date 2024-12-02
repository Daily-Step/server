package com.challenge.domain.challenge;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.category.Category;
import com.challenge.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @OneToMany(mappedBy = "challenge")
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

    @Column(nullable = false)
    private int durationInWeeks;

    @Column(nullable = false)
    private int weeklyGoalCount;

    @Column(nullable = false)
    private int totalGoalCount;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Builder
    private Challenge(List<Record> records, Member member, Category category, String title, String content,
                      int durationInWeeks, int weeklyGoalCount, int totalGoalCount, String color, boolean isDeleted,
                      LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.records = records;
        this.member = member;
        this.category = category;
        this.title = title;
        this.content = content;
        this.durationInWeeks = durationInWeeks;
        this.weeklyGoalCount = weeklyGoalCount;
        this.totalGoalCount = totalGoalCount;
        this.color = color;
        this.isDeleted = isDeleted;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

}
