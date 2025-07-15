package com.ohgiraffers.togedaeng.backend.domain.personality.entity;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Dog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "personality_combinations")
public class PersonalityCombination {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 반려견 ID
	@OneToOne
	@JoinColumn(name = "dog_id", nullable = false, unique = true)
	private Dog dog;

	// 성격 ID 1
	@Column(name = "personality_id_1", nullable = false)
	private Long personalityId1;

	// 성격 ID 2
	@Column(name = "personality_id_2")
	private Long personalityId2;
}
