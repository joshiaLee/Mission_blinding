package com.example.lion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class LionApplicationTests {

	@Test
	void contextLoads() {
		String fullPath = "/tmp/tomcat.8080.17288858922850441020/work/Tomcat/localhost/ROOT/nested:/home/ubuntu/Mission_blinding/lion/build/xxx";
		String matchedPath = extractMatchedPath(fullPath);

		System.out.println("Matched path: " + matchedPath);
	}


	private static String extractMatchedPath(String fullPath) {
		// 정규식 패턴 설정
		String pattern = "(/home.+?)/build";
		Pattern regex = Pattern.compile(pattern);

		// 매칭 수행
		Matcher matcher = regex.matcher(fullPath);
		if (matcher.find()) {
			// 매칭된 부분 반환
			return matcher.group(1);
		} else {
			// 매칭되지 않은 경우 예외처리 또는 기본값 반환 가능
			return "";
		}
	}

}
