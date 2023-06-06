package com.back.moment.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Uploader {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getSize());
        amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), objMeta);

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void delete(String imgUrl) {
        try {
            String[] temp = imgUrl.split("/");
            String fileKey = temp[temp.length-1];
            amazonS3.deleteObject(bucket, fileKey);
        } catch (Exception ignored) {
        }
    }

    public void deleteBatch(List<String> urls) {
        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucket);
        deleteRequest.withKeys(urls.toArray(new String[0]));

        try {
            DeleteObjectsResult result = amazonS3.deleteObjects(deleteRequest);
            List<DeleteObjectsResult.DeletedObject> deletedObjects = result.getDeletedObjects();

            // Optional: Handle deleted objects if needed
            for (DeleteObjectsResult.DeletedObject deletedObject : deletedObjects) {
                System.out.println("Deleted object: " + deletedObject.getKey());
            }
        } catch (MultiObjectDeleteException e) {
            // Handle exception if some objects failed to delete
            List<MultiObjectDeleteException.DeleteError> errors = e.getErrors();

            for (MultiObjectDeleteException.DeleteError error : errors) {
                System.out.println("Failed to delete object: " + error.getKey());
            }
        }
    }
}
