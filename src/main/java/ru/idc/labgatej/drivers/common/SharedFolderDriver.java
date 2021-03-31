package ru.idc.labgatej.drivers.common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.model.PacketInfo;

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
import java.util.function.Function;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public abstract class SharedFolderDriver
implements IDriver
{
    final Logger log = LoggerFactory.getLogger(SharedFolderDriver.class);

    protected DBManager dbManager;
    private Path dir2scan;
    private Path dirProcessed;
    protected String deviceCode;

    @Override
    public void init(
        ComboPooledDataSource connectionPool,
        Configuration config)
    {
        dir2scan = Paths.get(config.getParamValue("dir2scan"));
        dirProcessed = dir2scan.resolve("processedFiles");
        deviceCode = config.getParamValue("code");
        this.dbManager = new DBManager();
        dbManager.init(connectionPool);
        try {
            if (!Files.exists(dirProcessed)) {
                Files.createDirectory(dirProcessed);
            }
        } catch (IOException e) {
            log.error("Ошибка инициализации", e);
        }
    }

    @Override
    public void loop() throws IOException, InterruptedException {
        while (true) {
            scanFiles(dir2scan, this::processFile);
            Thread.sleep(30000);
        }
    }

    protected void scanFiles(
        Path directory,
        Function<Path, Exception> consumer)
    throws IOException
    {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (directory.equals(dir)) {
                    return CONTINUE;
                } else {
                    return SKIP_SUBTREE;
                }
            }

            @Override
            public FileVisitResult visitFile(
                Path file,
                BasicFileAttributes attr)
            throws IOException
            {
                Exception e = consumer.apply(file);

                if (e != null) {
                    log.error("Ошибка обработки файла!", e);
                    throw new IOException("Ошибка обработки файла: " + file);
                }

                return CONTINUE;
            }
        });
    }

    private Exception processFile (
        Path file)
    {
        try
        {
            List<PacketInfo> packets = parseFile(file);

            dbManager.savePakets(packets, false);

            Files.move(file, Paths.get(dirProcessed.toFile().getPath() + "/" + file.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
            log.debug(file.toAbsolutePath().toString());
        } catch (SQLException e) {
            log.error("Ошибка базы данных", e);
            return e;
        } catch (IOException e) {
            log.error("Ошибка переноса файла", e);
            return e;
        }

        return null;
    }

    public abstract List<PacketInfo> parseFile(Path file)
    throws IOException;

    @Override
    public void close() {

    }
}
