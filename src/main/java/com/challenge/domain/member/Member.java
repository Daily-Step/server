package com.challenge.domain.member;

import com.challenge.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "profile_img", length = 1000)
    private String profileImg;

    @Column(name = "is_notification_received", nullable = false)
    private Boolean isNotificationReceived;

    @Column(name = "login_type", nullable = false)
    private String loginType;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @Column(name = "deleted_at", length = 13)
    private String deletedAt;

}
