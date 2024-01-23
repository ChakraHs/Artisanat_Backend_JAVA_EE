package com.Pf_Artis.service.facade;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.Pf_Artis.dto.ImageDto;

public interface ImageServiceInterface {

	public ImageDto createImage(ImageDto imageDto)throws NotFoundException;
	
    public ImageDto readImage(Integer id);
    
    public ImageDto updateImage(ImageDto imageDto)throws NotFoundException;
    
    public void deleteImage(Integer id);
    
    public List<ImageDto> getAllImages();
	
    public List<ImageDto> getImagesByProduit( Integer produitId );

	ImageDto getLastImages();
}
