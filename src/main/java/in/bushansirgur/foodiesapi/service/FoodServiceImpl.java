package in.bushansirgur.foodiesapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public  class FoodServiceImpl implements FoodService{
private final S3Client s3Client;
@Value("${aws.s3.bucketname}")
private String bucketName;
    @Override
    public String uploadFile(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }
String originalFilename = file.getOriginalFilename();
String filenameExtension = "bin";
if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
    filenameExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
}
       String key= UUID.randomUUID().toString()+"."+filenameExtension;
       try {
           PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                   .bucket(bucketName)
                   .key(key)
                   .contentType(contentType)
                   .build();
           PutObjectResponse response=s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
       if(response.sdkHttpResponse().isSuccessful()){
           return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(key).build()).toExternalForm();
       }else{
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"File upload failed");
       }
       }catch (IOException ex){
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An error occured while loading the file");
       } catch (S3Exception | SdkClientException ex) {
           String reason;
           if (ex instanceof S3Exception s3Exception && s3Exception.awsErrorDetails() != null) {
               reason = s3Exception.awsErrorDetails().errorMessage();
           } else {
               reason = ex.getMessage();
           }
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to upload file to S3: " + reason);
       }
    }
}
