package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvTests {
    private static final Map<String, String> AUTHOR_BOOK_MAP = Collections.unmodifiableMap(new LinkedHashMap<>() {
        {
            put("kendrick", "west");
            put("kanye", "how to make music");
            put("mike", "how to make popcorn");
        }
    });

    private static final String[] HEADERS = {"author", "title"};
    private static final String EXPECTED_FILESTREAM = "author,title\n" +
            "kendrick,west\n" +
            "kanye,how to make music\n" +
            "mike,how to make popcorn";

    @Test
    void read() throws IOException {
        Reader in = new FileReader("src/test/resources/book.csv");

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        CSVParser records = csvFormat.parse(in);

        for (CSVRecord record : records) {
            String author = record.get("author");
            String title = record.get("title");
            assertThat(AUTHOR_BOOK_MAP.get(author)).isEqualTo(title);
        }
//        records.stream()
//                .forEach(r -> {
//                    String author = r.get("author");
//                    String title = r.get("title");
//                    assertThat(AUTHOR_BOOK_MAP.get(author)).isEqualTo(title);
//                });
    }

    @Test
    void read2() throws IOException {
        Reader in = new FileReader("src/test/resources/book.csv");

        for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {
            String columnOne = record.get(0);
            String columnTwo = record.get(1);
            System.out.println(columnOne);
            System.out.println(columnTwo);
        }
    }

    enum BookHeaders {
        author, title
    }

    @Test
    void readWithEnum() throws IOException {
        Reader in = new FileReader("src/test/resources/book.csv");
        CSVFormat csvFormat = CSVFormat.Builder
                .create()
                .setHeader(BookHeaders.class)
                .setSkipHeaderRecord(true)
                .build();

        CSVParser records = csvFormat.parse(in);
        for (CSVRecord record : records.getRecords()) {
            String author = record.get(BookHeaders.author);
            String title = record.get(BookHeaders.title);
            assertThat(AUTHOR_BOOK_MAP.get(author)).isEqualTo(title);
        }

    }

    @Test
    void write() throws IOException {
        //　書き出す場所調整
        // FileWriter out = new FileWriter("book_new.csv");
        StringWriter out = new StringWriter();
        CSVFormat csvFormat = CSVFormat.Builder
                .create()
                .setHeader(HEADERS)
                .setRecordSeparator("\n")
                .build();

        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            AUTHOR_BOOK_MAP.forEach((author, title) -> {
                try {
                    printer.printRecord(author, title);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        assertThat(EXPECTED_FILESTREAM).isEqualTo(out.toString().trim());
    }
}
