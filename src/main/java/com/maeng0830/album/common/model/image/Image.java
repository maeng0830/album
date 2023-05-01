package com.maeng0830.album.common.model.image;

import com.maeng0830.album.common.filedir.FileDir;
import java.util.UUID;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Embeddable
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

	private String imageOriginalName;
	private String imageStoreName;
	private String imagePath;

	public Image(MultipartFile imageFile, FileDir fileDir) {
		this.imageOriginalName = imageFile.getOriginalFilename();
		this.imageStoreName = UUID.randomUUID() + extractExt(this.imageOriginalName);
		this.imagePath = fileDir.getDir() + this.imageStoreName;
	}

	private String extractExt(String imageOriginalName) {
		int pos = imageOriginalName.lastIndexOf(".");
		return imageOriginalName.substring(pos);
	}
}
