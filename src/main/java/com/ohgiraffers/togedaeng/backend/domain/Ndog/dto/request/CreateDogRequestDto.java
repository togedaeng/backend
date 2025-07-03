package com.ohgiraffers.togedaeng.backend.domain.Ndog.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ohgiraffers.togedaeng.backend.domain.Ndog.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDogRequestDto {

	@NotBlank(message = "강아지 이름은 필수입니다.")
	private String name;

	@NotNull(message = "강아지 성별은 필수입니다.")
	private Gender gender;

	@NotNull(message = "생년월일은 필수입니다.")
	private LocalDate birth;

	@NotBlank(message = "애칭은 필수입니다.")
	private String callName;

	@NotNull(message = "성격 하나는 반드시 선택해야 합니다.")
	private Long personalityId1;

	private Long personalityId2;

	@NotNull(message = "메인 이미지는 필수입니다.")
	private MultipartFile mainImage;

	@Size(max = 3, message = "서브 이미지는 최대 3장까지 등록 가능합니다.")
	private List<MultipartFile> subImages;

	@Override
	public String toString() {
		return "CreateDogRequestDto{" +
			"name='" + name + '\'' +
			", gender=" + gender +
			", birth=" + birth +
			", callName='" + callName + '\'' +
			", personalityId1=" + personalityId1 +
			", personalityId2=" + personalityId2 +
			", mainImage=" + mainImage +
			", subImages=" + subImages +
			'}';
	}
}
