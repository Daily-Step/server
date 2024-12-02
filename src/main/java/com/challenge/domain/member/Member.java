package com.challenge.domain.member;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.job.Job;
import com.challenge.domain.notification.Notification;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private Long socialId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private JobYear jobYear;

    @Column(length = 1000)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private LoginType loginType;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isNotificationReceived = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Challenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    public static Member create(Long socialId, String email, String nickname, LocalDate birth, Gender gender,
            JobYear jobYear, LoginType loginType, Job job) {
        return Member.builder()
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .birth(birth)
                .gender(gender)
                .jobYear(jobYear)
                .loginType(loginType)
                .job(job)
                .build();
    }

    @Builder
    private Member(Long socialId, String email, String nickname, LocalDate birth, Gender gender, JobYear jobYear,
            LoginType loginType, Job job) {
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobYear = jobYear;
        this.loginType = loginType;
        this.job = job;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}
