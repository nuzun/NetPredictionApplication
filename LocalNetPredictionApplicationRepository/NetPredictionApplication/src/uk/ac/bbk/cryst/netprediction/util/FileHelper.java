package uk.ac.bbk.cryst.netprediction.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

	public static void createDirectory(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory " + path + " is created!");
			} else {
				System.out.println("Failed to create directory: " + path + "!");
			}
		}

	}

	public static void writeToFile(File file, String fullContent) throws IOException {
		// TODO Auto-generated method stub
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fullContent);
		bw.close();
	}
}
