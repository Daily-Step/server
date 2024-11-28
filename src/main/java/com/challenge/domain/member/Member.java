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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(length = 1000)
    private String profileImg;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isNotificationReceived;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private LoginType loginType;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Challenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

}
