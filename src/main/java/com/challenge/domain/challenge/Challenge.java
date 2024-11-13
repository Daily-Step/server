package com.challenge.domain.challenge;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.category.Category;
import com.challenge.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "challenge")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Challenge extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "color", nullable = false, length = 10)
    private String color;

    @Column(name = "weekly_goal_count", nullable = false)
    private Integer weeklyGoalCount;

    @Column(name = "total_goal_count", nullable = false)
    private Integer totalGoalCount;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
