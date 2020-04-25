package ru.idc.citm.drivers;

import ru.idc.citm.base.Configuration;
import ru.idc.citm.base.DBManager;
import ru.idc.citm.base.IDriver;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class Medonic implements IDriver {
	private DBManager dbManager;
	private Path dir2scan;
	private Path dirProcessed;

	@Override
	public void init(DBManager dbManager, Configuration config) {
		dir2scan = Paths.get(config.getParamValue("dir2scan"));
		dirProcessed = dir2scan.resolve("processedFiles");
	}

	@Override
	public void loop() throws IOException, InterruptedException {
		while (true) {
			scanFiles(dir2scan);
			//dbManager.saveResults(packetInfo);
			Thread.sleep(30000);
		}
	}

	private void scanFiles(Path dir) throws IOException {
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.equals(dir)) {
					return CONTINUE;
				} else {
					return SKIP_SUBTREE;
				}
			}

			@Override
			public FileVisitResult visitFile(Path file,	BasicFileAttributes attr) {
				try {
					processFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return CONTINUE;
			}
		});
	}

	private void processFile (Path file) throws IOException {
		Files.move(file, dirProcessed,
			StandardCopyOption.REPLACE_EXISTING);
		System.out.println(file.toAbsolutePath());
	}
}
