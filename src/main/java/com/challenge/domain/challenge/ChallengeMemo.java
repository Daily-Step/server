package com.challenge.domain.challenge;

import com.challenge.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "challenge_memo")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChallengeMemo extends BaseDateTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_memo_id")
    private Long id;
    
    @Column(name = "date", nullable = false)
    private Date date;
    
    @Column(name = "content", length = 500)
    private String content;
    
    @Column(name = "img", length = 1000)
    private String img;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;
}
