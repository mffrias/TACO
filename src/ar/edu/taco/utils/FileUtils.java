/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	static final private String NORMALIZATED_FILE_SEPARATOR = "/";
	
	public static String readFile(String filename) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		reader.close();
		return builder.toString();
	}
	
	public static void writeToFile(String path, String fileContent) throws IOException {
		String normalizatedFileName = normalizateFileName(path);				
		int slashPosition = normalizatedFileName.lastIndexOf(NORMALIZATED_FILE_SEPARATOR);
		if (slashPosition >= 0)  {
			File aFile = new File(normalizatedFileName.substring(0, slashPosition));
			if (!aFile.exists()) {
				aFile.mkdirs();
			}
		}
		
		FileWriter fileWriter = new FileWriter(path);
		fileWriter.write(fileContent);
		fileWriter.close();
	}
	
	public static void appendToFile(String path, String fileContent) throws IOException {
	    String normalizatedFileName = normalizateFileName(path);                
	    int slashPosition = normalizatedFileName.lastIndexOf(NORMALIZATED_FILE_SEPARATOR);
	    if (slashPosition >= 0)  {
	        File aFile = new File(normalizatedFileName.substring(0, slashPosition));
	        if (!aFile.exists()) {
	            aFile.mkdirs();
	        }
	    }

	    FileWriter fileWriter = new FileWriter(path, true);
	    fileWriter.write(fileContent);
	    fileWriter.close();
	}
	
	private static String normalizateFileName(String path) {
		String normalizatedFileName;
		if (File.separator.equals("\\")) {
			normalizatedFileName = path.replaceAll("\\\\",NORMALIZATED_FILE_SEPARATOR );
		} else {
			normalizatedFileName = path;
		}
		return normalizatedFileName;
	}
}