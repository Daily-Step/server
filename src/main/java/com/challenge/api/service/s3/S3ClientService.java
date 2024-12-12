package com.challenge.api.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ClientService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * multipartFile을 s3에 업로드 후 저장된 url 리턴하는 메소드
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile multipartFile) throws IOException {
        String randomName = UUID.randomUUID().toString();

        // 이미지 경로 지정
        String dirName = "image";
        String fileName = dirName + "/" + randomName;

        // put 요청 생성 및 s3 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));

        // 업로드한 파일의 url 받아오기
        GetUrlRequest request = GetUrlRequest.builder().bucket(bucket).key(fileName).build();

        return s3Client.utilities().getUrl(request).toString();
    }

}
