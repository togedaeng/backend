// package com.ohgiraffers.togedaeng.backend.domain.dog.service;
//
// import java.awt.image.BufferedImage;
// import java.io.ByteArrayOutputStream;
// import java.util.List;
// import java.util.Map;
//
// import javax.imageio.ImageIO;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.multipart.MultipartFile;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class GptImageService {
//
// 	private static final Logger log = LoggerFactory.getLogger(GptImageService.class);
//
// 	@Value("${openai.api-key}")
// 	private String apiKey;
//
// 	private static final String FIXED_PROMPT =
// 		"A hyper-realistic 3D render of a dog, standing on all four legs, posed at a 45° angle toward the camera.\n"
// 			+ "The dog's head and hindquarters are aligned along an invisible guideline (like the ends of a spread ruler), creating balanced 구도.\n"
// 			+ "Soft, diffuse studio lighting highlights the fur texture.\n"
// 			+ "Background is fully transparent.";
//
// 	public String generateDogImageFromUserImageWithPrompt(MultipartFile userImage, String prompt) {
// 		try {
// 			// 1. 이미지 PNG로 변환
// 			BufferedImage bufferedImage = ImageIO.read(userImage.getInputStream());
// 			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
// 			ImageIO.write(bufferedImage, "png", pngOutputStream);
// 			byte[] pngBytes = pngOutputStream.toByteArray();
//
// 			ByteArrayResource imageResource = new ByteArrayResource(pngBytes) {
// 				@Override
// 				public String getFilename() {
// 					return "image.png";
// 				}
// 			};
//
// 			// 2. 요청 구성
// 			HttpHeaders headers = new HttpHeaders();
// 			headers.setBearerAuth(apiKey);
// 			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
// 			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
// 			body.add("image", imageResource);
// 			body.add("prompt", prompt);
// 			body.add("n", "1");
// 			body.add("size", "1024x1024");
//
// 			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
//
// 			RestTemplate restTemplate = new RestTemplate();
//
// 			ResponseEntity<Map> response = restTemplate.postForEntity(
// 				"https://api.openai.com/v1/images/edits",
// 				request,
// 				Map.class
// 			);
//
// 			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
// 				List<Map<String, String>> data = (List<Map<String, String>>)response.getBody().get("data");
// 				if (data != null && !data.isEmpty()) {
// 					String url = data.get(0).get("url");
// 					log.info("✅ GPT 이미지 생성 완료: {}", url);
// 					return url;
// 				}
// 			}
//
// 			throw new RuntimeException("❌ GPT 이미지 생성 실패");
//
// 		} catch (Exception e) {
// 			log.error("GPT 이미지 생성 중 오류", e);
// 			throw new RuntimeException("GPT 이미지 생성 중 예외 발생", e);
// 		}
// 	}
//
// 	public String getFixedPrompt() {
// 		return FIXED_PROMPT;
// 	}
// }
