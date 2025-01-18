package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.category.Category;
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.domain.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChallengeRecord> challengeRecords = new ArrayList<>();

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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private ChallengeStatus status;

    @Column(nullable = false)
    private int durationInWeeks;

    @Column(nullable = false)
    private int weeklyGoalCount;

    @Column(nullable = false)
    private int totalGoalCount;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Builder
    private Challenge(Member member, Category category, String title, String content, ChallengeStatus status,
                      int durationInWeeks, int weeklyGoalCount, String color, LocalDateTime startDateTime) {
        this.challengeRecords = new ArrayList<>();
        this.member = member;
        this.category = category;
        this.title = title;
        this.content = content;
        this.status = status;
        this.durationInWeeks = durationInWeeks;
        this.weeklyGoalCount = weeklyGoalCount;
        this.totalGoalCount = durationInWeeks * weeklyGoalCount;
        this.color = color;
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
                .status(ChallengeStatus.ONGOING)
                .durationInWeeks(request.getDurationInWeeks())
                .weeklyGoalCount(request.getWeeklyGoalCount())
                .color(request.getColor())
                .startDateTime(startDateTime)
                .build();
    }

    public void update(Category category, ChallengeUpdateServiceRequest request) {
        this.title = request.getTitle();
        this.category = category;
        this.color = request.getColor();
        this.content = request.getContent();
    }

    public void addRecord(ChallengeRecord challengeRecord) {
        this.challengeRecords.add(challengeRecord);
    }

    public void success() {
        this.status = ChallengeStatus.SUCCEED;
    }

    public void expire() {
        this.status = ChallengeStatus.EXPIRED;
    }

    public void delete() {
        this.status = ChallengeStatus.REMOVED;
    }

}
