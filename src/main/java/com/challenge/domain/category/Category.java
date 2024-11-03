package com.challenge.domain.category;

import com.challenge.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    
    @Column(name="title",nullable = false,length=50)
    private String title;
    
    @Column(name="is_deleted",nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
}
