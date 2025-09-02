// package com.ohgiraffers.togedaeng.backend.domain.custom.service;
//
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.UUID;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Service
// public class FileUploadService {
//
//   private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//   @Value("${file.upload.path}")
//   private String uploadPath;
//
//   @Value("${file.upload.url-prefix}")
//   private String urlPrefix;
//
//   /**
//    * 파일을 로컬 디렉토리에 업로드하고 접근 가능한 URL을 반환합니다.
//    *
//    * @param file    업로드할 파일
//    * @param dirName 저장할 디렉토리명
//    * @return 파일 접근 URL
//    * @throws IOException 파일 저장 중 오류 발생 시
//    */
//   public String upload(MultipartFile file, String dirName) throws IOException {
//     // 1. 현재 설정 확인
//     log.info("=== 파일 업로드 시작 ===");
//     log.info("설정된 업로드 경로: {}", uploadPath);
//     log.info("파일명: {}", file.getOriginalFilename());
//     log.info("파일 크기: {} bytes", file.getSize());
//
//     // 2. 디렉토리 경로 생성
//     Path uploadDir = Paths.get(uploadPath, dirName);
//     log.info("생성할 디렉토리 경로: {}", uploadDir.toAbsolutePath());
//
//     // 3. 디렉토리 존재 확인 및 생성
//     if (!Files.exists(uploadDir)) {
//         log.info("디렉토리가 존재하지 않아 생성합니다: {}", uploadDir);
//         try {
//             Files.createDirectories(uploadDir);
//             log.info("디렉토리 생성 성공");
//         } catch (Exception e) {
//             log.error("디렉토리 생성 실패: {}", e.getMessage(), e);
//             throw e;
//         }
//     } else {
//         log.info("디렉토리가 이미 존재합니다: {}", uploadDir);
//     }
//
//     // 4. 파일명 생성
//     String originalFilename = file.getOriginalFilename();
//     String fileExtension = "";
//     if (originalFilename != null && originalFilename.contains(".")) {
//         fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//     }
//     String fileName = UUID.randomUUID().toString() + fileExtension;
//     log.info("생성된 파일명: {}", fileName);
//
//     // 5. 파일 저장
//     Path filePath = uploadDir.resolve(fileName);
//     log.info("파일을 저장할 경로: {}", filePath.toAbsolutePath());
//
//     try {
//         Files.copy(file.getInputStream(), filePath);
//         log.info("파일 복사 완료");
//
//         // 6. 파일 저장 확인
//         if (Files.exists(filePath)) {
//             long fileSize = Files.size(filePath);
//             log.info("파일이 성공적으로 저장되었습니다: {}", filePath);
//             log.info("저장된 파일 크기: {} bytes", fileSize);
//
//             // 7. 파일 읽기 테스트
//             byte[] testRead = Files.readAllBytes(filePath);
//             log.info("파일 읽기 테스트 성공: {} bytes 읽음", testRead.length);
//         } else {
//             log.error("파일 저장에 실패했습니다: {}", filePath);
//             throw new IOException("파일 저장 실패");
//         }
//     } catch (Exception e) {
//         log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
//         throw e;
//     }
//
//     // 8. URL 생성
//     String fileUrl = urlPrefix + "/" + dirName + "/" + fileName;
//     log.info("생성된 파일 URL: {}", fileUrl);
//     log.info("=== 파일 업로드 완료 ===");
//
//     return fileUrl;
//   }
//
//   /**
//    * 파일 URL을 받아서 로컬 파일 시스템에서 삭제합니다.
//    *
//    * @param fileUrl 삭제할 파일의 URL
//    */
//   public void delete(String fileUrl) {
//     if (fileUrl == null || fileUrl.isEmpty()) {
//       return;
//     }
//
//     try {
//       // URL에서 파일 경로 추출
//       String filePath = fileUrl.replace(urlPrefix, "");
//       if (filePath.startsWith("/")) {
//         filePath = filePath.substring(1);
//       }
//
//       Path fullPath = Paths.get(uploadPath, filePath);
//
//       // 파일이 존재하면 삭제
//       if (Files.exists(fullPath)) {
//         Files.delete(fullPath);
//         log.info("파일 삭제 완료: {}", fileUrl);
//       } else {
//         log.warn("삭제할 파일이 존재하지 않습니다: {}", fileUrl);
//       }
//     } catch (Exception e) {
//       log.warn("파일 삭제 실패. URL: {}", fileUrl, e);
//     }
//   }
// }
