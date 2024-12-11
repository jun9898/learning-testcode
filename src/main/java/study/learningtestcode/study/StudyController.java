package study.learningtestcode.study;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.learningtestcode.domain.Study;

@RestController
@RequiredArgsConstructor
public class StudyController {

	private final StudyRepository repository;

	@GetMapping("/study/{id}")
	public Study getStudy(@PathVariable("id") Long id) {
		return repository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Study not found for '" + id + "'"));
	}

	@PostMapping("/study")
	public Study createsStudy(@RequestBody Study study) {
		return repository.save(study);
	}
}
