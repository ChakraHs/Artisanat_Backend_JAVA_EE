package com.Pf_Artis.service.facade;

import java.io.InputStream;
import java.util.List;

import com.Pf_Artis.models.Image;

public interface ImageServiceInterface {

	public Image createImage(Image image);
    public Image readImage(Long id);
    public Image updateImage(Image image);
    public void deleteImage(Long id);
    
    public List<Image> getAllImages();
    
	public Image createImage(Image image, InputStream inputStream, String fileName);
	
}
