package ru.idc.labgatej.drivers.common;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.model.PacketInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public abstract class SharedFolderDriver
implements IDriver
{
    final Logger log = LoggerFactory.getLogger(SharedFolderDriver.class);

    private DBManager dbManager;
    private Path dir2scan;
    private Path dirProcessed;
    protected String deviceCode;

    @Override
    public void init(DBManager dbManager, Configuration config) {
        dir2scan = Paths.get(config.getParamValue("dir2scan"));
        dirProcessed = dir2scan.resolve("processedFiles");
        deviceCode = config.getParamValue("code");
        this.dbManager = dbManager;
        try {
            if (!Files.exists(dirProcessed)) {
                Files.createDirectory(dirProcessed);
            }
        } catch (IOException e) {
            log.error("Ошибка инициализации", e);
        }
    }

    @Override
    public void loop() throws IOException, InterruptedException, SQLException {
        while (true) {
            scanFiles(dir2scan);
            Thread.sleep(30000);
        }
    }

    private void scanFiles(Path dir2scan) throws IOException, SQLException {
        Files.walkFileTree(dir2scan, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir2scan.equals(dir)) {
                    return CONTINUE;
                } else {
                    return SKIP_SUBTREE;
                }
            }

            @SneakyThrows(SQLException.class)
            @Override
            public FileVisitResult visitFile(Path file,	BasicFileAttributes attr) {
                try {
                    processFile(file);
                } catch (IOException e) {
                    log.error("Ошибка обработки файла", e);
                } catch (SQLException e) {
                    log.error("Ошибка обработки файла", e);
                    throw e;
                }

                return CONTINUE;
            }
        });
    }

    private void processFile (Path file) throws IOException, SQLException
    {
        List<PacketInfo> packets = parseFile(file);

        dbManager.savePakets(packets, false);

        Files.move(file, Paths.get(dirProcessed.toFile().getPath() + "/" + file.getFileName()),
            StandardCopyOption.REPLACE_EXISTING);
        log.debug(file.toAbsolutePath().toString());
    }

    public abstract List<PacketInfo> parseFile(Path file)
    throws FileNotFoundException;

    @Override
    public void close() {

    }
}
