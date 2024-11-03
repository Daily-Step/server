package com.challenge.domain.notification;

import com.challenge.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    
    @Column(name="title",nullable = false,length=50)
    private String title;
    
    @Column(name="content",nullable = false,length=500)
    private String content;
    
    @Column(name="is_send",nullable = false, columnDefinition = "boolean default false")
    private Boolean isSend;
    
    @Column(name="send_at",length=13)
    private String sendAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
