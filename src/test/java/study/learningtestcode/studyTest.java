package study.learningtestcode;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;

import study.learningtestcode.study.StudyStatus;

// 전략을 설정 가능 언더스코어를 공백으로 바꿔준다.
// @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class studyTest {

	@Test
	@DisplayName("특정 환경에서 작동하는 테스트 작성하기")
	// 이런식으로 작업도 가능하다.
	@EnabledOnOs(OS.MAC)
	// 자바 버전도 설정 가능
	@EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11, JRE.JAVA_17})
	// 그냥 어노테이션에 때려박을수도 있음
	@EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
	void create_new_study_3() {
		// 개발 환경이 LOCAL일때만 TEST코드를 실행
		String test_env = System.getenv("TEST_ENV");
		System.out.println(test_env);
		assumeTrue("LOCAL".equalsIgnoreCase(test_env));
		Study actual = new Study(10);
		assertThat(actual.getLimit()).isGreaterThan(0);
	}

	// display 전략은 메서드명을 스네이크 케이스로 길게 작성해서 동작을 설명하거나
	// 어노테이션 Display를 사용해 동작을 설명할 수 있음 (이모지 가능)
	@Order(1)
	@Test
	@DisplayName("스터디 만들기")
	@DisabledOnOs(OS.MAC)
	void create_new_study() {
		// 어떤 코드를 실행했을때 어떤 에러가 발생할지 검증할 수 있음
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
			() -> new Study(-10));
		String message = illegalArgumentException.getMessage();
		assertEquals("limit는 0보다 커야한다.", message);
		Study study = new Study(-10);
		assertNotNull(study);

		// AssertAll로 묶어서 람다식으로 진행하면 해결할 수 있음
		assertAll(
			() -> assertNotNull(study),
			() -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
					() -> "스터디를 처음 만들면 DRAFT 상태여야 한다. 현재 상태 :" + study.getStatus()),
			() -> assertTrue(study.getLimit() > 0, "스터디 참석 가능 인원을 0보다 커야한다.")
		);
		// 스트링을 줄 수 있고 람다로도 작성 할 수 있음
		// 첫번째 Assert에서 테스트가 깨지면 두번째 Assert는 실행되지 않는다.
		System.out.println("create");
	}

	@Test
	@DisplayName("스터디 만들기 fast")
	@Tag("Fast")
	void create_new_study_fast() {
		Study study = new Study(10);
		assertThat(study.getLimit()).isGreaterThan(0);
	}

	@FastTest
	void create_new_study_slow() {
		Study study = new Study(10);
		assertThat(study.getLimit()).isGreaterThan(0);
	}

	@Test
	// @Disabled
	@DisplayName("스터디 다시 만들기")
	void create_new_study_again() {
		// 동일하게 테스트가 실패하지만 테스트 실패 조건에 도달하면 즉시 테스트를 종료한다.
		assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
				new Study(10);
				Thread.sleep(300);
			}
		);
		System.out.println("create1");
	}

	@DisplayName("반복 테스트 ")
	@RepeatedTest(value = 10, name = "is {displayName}, ! {currentRepetition} / {totalRepetitions}")
	void repeatTest(RepetitionInfo repetitionInfo) {
		System.out.println("반복 테스트 " +
			repetitionInfo.getCurrentRepetition() + " \n" +
			repetitionInfo.getTotalRepetitions());
	}

	@DisplayName("스터디 만들기")
	@ParameterizedTest(name = "is {displayName} : {index} => message={0}")
	@CsvSource({"10, '자바 스터디'", "20, 스프링"})
	// void parameterizedTest(@ConvertWith(StudyConverter.class) Study study) {
	void parameterizedTest(@AggregateWith(StudyAggregator.class) Study study) {
		System.out.println(study);
	}

	static class StudyAggregator implements ArgumentsAggregator {
		@Override
		public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws
			ArgumentsAggregationException {
			return new Study(accessor.getInteger(0), accessor.getString(1));
		}
	}

	// 하나의 값만 컨버팅 가능
	static class StudyConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
			assertEquals(Study.class, targetType, "Can only convert Study objects");
			return new Study(Integer.parseInt(source.toString()));
		}
	}


	@BeforeAll
	static void beforeAll() {
		System.out.println("beforeAll");
		System.out.println("beforeAll은 static으로 선언해야함");
		System.out.println("private으로 선언하면 안됌");
		System.out.println("return 타입이 있으면 안됌");
		System.out.println("시작 전에 딱 한번만 호출이 된다");
	}

	@AfterAll
	static void afterAll() {
		System.out.println("전반적으로 beforeAll과 같음");
	}

	@BeforeEach
	void beforeEach() {
		System.out.println("각각의 테스트 전에 한번씩 실행");
	}

	@AfterEach
	void afterEach() {
		System.out.println("각각의 테스트 후에 한번씩 실행");
	}
}