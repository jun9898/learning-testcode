package study.learningtestcode.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.learningtestcode.study.StudyStatus;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Study {

	@Id
	@GeneratedValue
	private Long id;
	private StudyStatus status = StudyStatus.DRAFT;
	private int limitCount;
	private String name;
	private LocalDateTime openedDateTime;
	private Long ownerId;

	public Study(int limit, String name) {
		this.limitCount = limit;
		this.name = name;
	}

	public Study(int limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("limit은 0보다 커야 한다.");
		}
		this.limitCount = limit;
	}

	public void publish() {
		this.openedDateTime = LocalDateTime.now();
		this.status = StudyStatus.OPENED;
	}
}
