package com.maeng0830.album.common.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.maeng0830.album.common.exception.AlbumException;
import com.maeng0830.album.common.exception.code.ApiExceptionCode;
import com.maeng0830.album.common.model.image.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class AwsS3Manager {

	private static final String S3_BUCKET_DIRECTORY = "static/";
	private final AmazonS3Client amazonS3Client;
	private final String s3Bucket;

	public List<Image> uploadImage(List<MultipartFile> imageFiles) {
		List<Image> images = new ArrayList<>();

		for (MultipartFile imageFile : imageFiles) {
			ObjectMetadata objectMetadata = createObjectMetadata(imageFile);

			String storeName = UUID.randomUUID() + "." + getFileExtension(imageFile.getOriginalFilename());

			String storeNameWithDirectory = S3_BUCKET_DIRECTORY + storeName;

			try {
				amazonS3Client.putObject(new PutObjectRequest(s3Bucket, storeNameWithDirectory, imageFile.getInputStream(), objectMetadata).withCannedAcl(
						CannedAccessControlList.PublicRead));

				Image image = createImage(imageFile, storeName, storeNameWithDirectory);

				images.add(image);
			} catch (IOException e) {
				throw new AlbumException(ApiExceptionCode.FAIL_UPLOAD, e);
			}
		}

		return images;
	}

	public Image uploadImage(MultipartFile imageFile) {
		if (imageFile == null) {
			return null;
		}

		ObjectMetadata objectMetadata = createObjectMetadata(imageFile);

		String storeName = UUID.randomUUID() + "." + imageFile.getOriginalFilename();

		String storeNameWithDirectory = S3_BUCKET_DIRECTORY + storeName;

		try {
			amazonS3Client.putObject(new PutObjectRequest(s3Bucket, storeNameWithDirectory, imageFile.getInputStream(), objectMetadata).withCannedAcl(
					CannedAccessControlList.PublicRead));

			return createImage(imageFile, storeName, storeNameWithDirectory);
		} catch (IOException e) {
			throw new AlbumException(ApiExceptionCode.FAIL_UPLOAD, e);
		}
	}

	private ObjectMetadata createObjectMetadata(MultipartFile imageFile) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(imageFile.getContentType());
		objectMetadata.setContentLength(imageFile.getSize());
		return objectMetadata;
	}

	private Image createImage(MultipartFile imageFile, String storeName, String storeNameWithDirectory) {
		return Image.builder()
				.imageOriginalName(imageFile.getOriginalFilename())
				.imageStoreName(storeName)
				.imagePath(amazonS3Client.getUrl(s3Bucket, storeNameWithDirectory).toString())
				.build();
	}

	private String getFileExtension(String originalFilename) {
		if (originalFilename == null) {
			throw new AlbumException(ApiExceptionCode.FAIL_UPLOAD);
		}

		int index = originalFilename.lastIndexOf(".");

		return originalFilename.substring(index + 1);
	}
}
