package com.challenge.domain.notification;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    private LocalDateTime sendAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Notification of(String title, String content, LocalDateTime sendAt, Member member) {
        return Notification.builder()
                .title(title)
                .content(content)
                .sendAt(sendAt)
                .member(member)
                .build();
    }

    @Builder
    private Notification(String title, String content, LocalDateTime sendAt, Member member) {
        this.title = title;
        this.content = content;
        this.sendAt = sendAt;
        this.member = member;
    }

}
