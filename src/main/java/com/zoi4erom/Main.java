package com.zoi4erom;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.*;
import static java.lang.System.getProperty;
import static java.lang.System.in;
import static java.lang.System.lineSeparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(in);
		out.println("Zoi4erom company");
		out.println("Все що неє текстовим файликом буде проігноровано");
		out.println("картинка, відео, гіфка....");
		out.print("Напишіть фулл шлях на директорію: ");
		String directoryPath = scanner.nextLine();
		scanner.close();

		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			out.println("Директорія не існує або це не директорія.");
			return;
		}

		String outputFilePath =
		    getProperty("user.home") + File.separator + "Desktop" + File.separator
			  + "output.txt";
		long startTime = currentTimeMillis();

		StringBuilder contentBuilder = new StringBuilder();
		int[] statistics = new int[2];

		try {
			statistics = collectDirectoryContent(directory, contentBuilder);
		} catch (IOException e) {
			err.println(
			    "Сталася помилка під час збирання вмісту директорії: " + e.getMessage());
			return;
		}

		long endTime = currentTimeMillis();
		long duration = endTime - startTime;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
			writer.write("Загальна кількість файлів: " + statistics[0]
			    + ", Загальна кількість рядків: " + statistics[1] + ", Час виконання: "
			    + duration + " мс");
			writer.newLine();
			writer.write(contentBuilder.toString());
			out.println("Вміст збережено в файл: " + outputFilePath);
			out.println("Загальна кількість файлів: " + statistics[0]);
			out.println("Загальна кількість рядків: " + statistics[1]);
			out.println("Час виконання: " + duration + " мс");
		} catch (IOException e) {
			err.println("Сталася помилка під час запису файлу: " + e.getMessage());
		}
	}

	private static int[] collectDirectoryContent(File directory, StringBuilder contentBuilder)
	    throws IOException {
		File[] files = directory.listFiles();
		int fileCount = 0;
		int lineCount = 0;
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && isTextFile(file.toPath())) {
					fileCount++;
					contentBuilder.append(file.getName()).append(lineSeparator());
					int lines = readFileContent(file, contentBuilder);
					lineCount += lines;
					contentBuilder.append(lineSeparator());
				} else if (file.isDirectory()) {
					int[] subDirStats = collectDirectoryContent(file, contentBuilder);
					fileCount += subDirStats[0];
					lineCount += subDirStats[1];
				}
			}
		}
		return new int[]{fileCount, lineCount};
	}

	private static boolean isTextFile(Path filePath) {
		try {
			String mimeType = Files.probeContentType(filePath);
			return mimeType != null && mimeType.startsWith("text");
		} catch (IOException e) {
			err.println("Не вдалося визначити тип файлу: " + e.getMessage());
			return false;
		}
	}

	private static int readFileContent(File file, StringBuilder contentBuilder)
	    throws IOException {
		int lineCount = 0;
		try (FileReader fr = new FileReader(file); Scanner fileScanner = new Scanner(fr)) {
			while (fileScanner.hasNextLine()) {
				contentBuilder.append(fileScanner.nextLine())
				    .append(lineSeparator());
				lineCount++;
			}
		}
		return lineCount;
	}
}
