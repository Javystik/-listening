package com.zoi4erom;

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
		Scanner scanner = new Scanner(System.in);
		System.out.println("Напишіть фулл шлях на директорію: ");
		String directoryPath = scanner.nextLine();
		scanner.close();

		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			System.out.println("Директорія не існує або це не директорія.");
			return;
		}

		String outputFilePath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "output.txt";
		long startTime = System.currentTimeMillis();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
			int[] statistics = writeDirectoryContent(directory, writer);
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			writer.write("Загальна кількість файлів: " + statistics[0] + ", Загальна кількість рядків: " + statistics[1] + ", Час виконання: " + duration + " мс");
			writer.newLine();
			System.out.println("Вміст збережено в файл: " + outputFilePath);
			System.out.println("Загальна кількість файлів: " + statistics[0]);
			System.out.println("Загальна кількість рядків: " + statistics[1]);
			System.out.println("Час виконання: " + duration + " мс");
		} catch (IOException e) {
			System.err.println("Сталася помилка під час запису файлу: " + e.getMessage());
		}
	}

	private static int[] writeDirectoryContent(File directory, BufferedWriter writer) throws IOException {
		File[] files = directory.listFiles();
		int fileCount = 0;
		int lineCount = 0;
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && isTextFile(file.toPath())) { // Process only text files
					fileCount++;
					writer.write(file.getName());
					writer.newLine();
					int lines = writeFileContent(file, writer);
					lineCount += lines;
					writer.newLine();
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
			System.err.println("Не вдалося визначити тип файлу: " + e.getMessage());
			return false;
		}
	}

	private static int writeFileContent(File file, BufferedWriter writer) throws IOException {
		int lineCount = 0;
		try (FileReader fr = new FileReader(file); Scanner fileScanner = new Scanner(fr)) {
			while (fileScanner.hasNextLine()) {
				writer.write(fileScanner.nextLine());
				writer.newLine();
				lineCount++;
			}
		}
		return lineCount;
	}
}
