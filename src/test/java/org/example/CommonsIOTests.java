package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonsIOTests {

    @Test
    void copyAndRead() throws IOException {
        String expected = "hello world from file.txt.";
        File file = FileUtils.getFile(Objects.requireNonNull(getClass().getClassLoader().getResource("file.txt")).getPath());
        File tempDir = FileUtils.getTempDirectory();

        FileUtils.copyFileToDirectory(file, tempDir);
        File tempFile = FileUtils.getFile(tempDir, file.getName());
        String data = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        assertThat(data).isEqualTo(expected);
    }

    @Test
    void filename() {
        String path = Objects.requireNonNull(getClass().getClassLoader().getResource("file.txt")).getPath();

        System.out.println(FilenameUtils.getFullPath(path));
        System.out.println(FilenameUtils.getExtension(path));
        System.out.println(FilenameUtils.getBaseName(path));
    }

    @Test
    void tee() throws IOException {
        final String s = "hello world";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();

        FilterOutputStream teeOutputStream = new TeeOutputStream(outputStream1, outputStream2);
        int read = new TeeInputStream(inputStream, teeOutputStream, true).read(new byte[s.length()]);
        System.out.println(read);

        assertThat(String.valueOf(outputStream1)).isEqualTo(s);
        assertThat(String.valueOf(outputStream2)).isEqualTo(s);
    }

    @Test
    void filter() {
        final String testFile = "file.txt";

        String path = Objects.requireNonNull(getClass().getClassLoader().getResource(testFile)).getPath();
        File dir = FileUtils.getFile(FilenameUtils.getFullPath(path));

        String[] names = { "notThisOne", testFile };

        String[] list = dir.list(new NameFileFilter(names, IOCase.INSENSITIVE));
        assertThat(Objects.requireNonNull(list)[0]).isEqualTo(testFile);

        String[] txts = dir.list(new AndFileFilter(
                new WildcardFileFilter("*ple*", IOCase.INSENSITIVE),
                new SuffixFileFilter("txt")
        ));
        assertThat(Objects.requireNonNull(txts)[0]).isEqualTo("SAMPLE2.txt");
    }

    @Test
    void comparator() {
        var pathFileComparator = new PathFileComparator(IOCase.INSENSITIVE);
        String path = FilenameUtils.getFullPath(Objects.requireNonNull(getClass().getClassLoader().getResource("file.txt")).getPath());
        File dir = new File(path);
        File[] files = dir.listFiles();

        pathFileComparator.sort(files);
        assertThat(Objects.requireNonNull(files)[0].getName()).isEqualTo("abc.memo");
    }
}
