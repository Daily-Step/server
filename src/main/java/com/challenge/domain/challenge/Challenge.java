package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.category.Category;
import com.challenge.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Challenge(Member member, Category category, String title, String content, int durationInWeeks,
            int weeklyGoalCount, String color, LocalDateTime startDateTime) {
        this.records = new ArrayList<>();
        this.member = member;
        this.category = category;
        this.title = title;
        this.content = content;
        this.durationInWeeks = durationInWeeks;
        this.weeklyGoalCount = weeklyGoalCount;
        this.totalGoalCount = durationInWeeks * weeklyGoalCount;
        this.color = color;
        this.isDeleted = false;
        this.startDateTime = startDateTime;
        this.endDateTime = startDateTime.plusWeeks(durationInWeeks)
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay()
                .minusSeconds(1);
    }

    public static Challenge create(Member member, Category category, ChallengeCreateServiceRequest request,
            LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .durationInWeeks(request.getDurationInWeeks())
                .weeklyGoalCount(request.getWeeklyGoalCount())
                .color(request.getColor())
                .startDateTime(startDateTime)
                .build();
    }

}
