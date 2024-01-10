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
		String inputString = "이것은 #java 코드입니다. #프로그래밍은 재미있습니다. #코딩 #자바개발자";

		List<String> hashWords = extractHashWords(inputString);

		System.out.println("추출된 단어들:");
		for (String word : hashWords) {
			System.out.println(word);
		}
	}


	private static List<String> extractHashWords(String input) {
		List<String> hashWords = new ArrayList<>();
		Pattern pattern = Pattern.compile("#([\\p{L}\\p{N}_]+)"); // #으로 시작하고, 그 뒤에 단어 문자(알파벳, 숫자, 언더스코어)가 하나 이상인 패턴

		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			String hashWord = matcher.group(1);
			hashWords.add(hashWord);
		}

		return hashWords;
	}

}
