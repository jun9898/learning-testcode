package study.learningtestcode.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Builder
@Getter @NoArgsConstructor @AllArgsConstructor
public class Member {

	@Id
	@GeneratedValue
	private Long id;

	private String email;

}
