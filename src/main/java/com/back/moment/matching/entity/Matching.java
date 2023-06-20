package com.back.moment.matching.entity;

import com.back.moment.common.TimeStamped;
import com.back.moment.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Matching extends TimeStamped {  // applicant and accepter 모두 승낙

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long boardId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_id")
	private Users applicant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accepter_id")
	private Users accepter;

	public Matching(Long boardId, Users applicant, Users accepter) {
		this.boardId = boardId;
		this.applicant = applicant;
		this.accepter = accepter;
	}
}
