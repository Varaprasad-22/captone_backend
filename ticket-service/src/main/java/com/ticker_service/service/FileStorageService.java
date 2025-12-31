package com.ticker_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

//import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	// Directory path where the uploaded files will be stored
	private static final String BASE_PATH = "storage/tickets_attachment/";

	public String save(MultipartFile file) {
		try {
			// it creates the file directorries
			Files.createDirectories(Paths.get(BASE_PATH));
			// see for path creation kept like the name and random string & unique name for
			// the file using UUID to avoid conflicts;
			String name = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path path = Paths.get(BASE_PATH, name);
			// Copy the file input stream to the specified path, replacing any existing file
			// with the same name
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			//return path to store it in db;
			return path.toString();
		} catch (IOException e) {
			throw new RuntimeException("File upload failed", e);
		}
	}
}
