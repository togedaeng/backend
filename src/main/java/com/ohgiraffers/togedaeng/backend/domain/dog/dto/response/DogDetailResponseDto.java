package com.ohgiraffers.togedaeng.backend.domain.dog.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Gender;
import com.ohgiraffers.togedaeng.backend.domain.dog.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DogDetailResponseDto {
  private Long id;
  private String name;
  private Gender gender;
  private LocalDate birth;
  private List<String> personalities;
  private List<String> callNames;
  private Status status;
  private List<String> ownerNicknames;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private String renderedUrl;
}