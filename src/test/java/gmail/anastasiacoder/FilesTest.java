package gmail.anastasiacoder;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilesTest extends TestBase {

    @Test
    @DisplayName("Имя файла отображается после загрузки")
    void filenameShouldDisplayedAfterUploadActionFromClasspathTest() {
        open("https://demoqa.com/upload-download");
        $("#uploadFile").uploadFromClasspath("test.txt");
        $("#uploadedFilePath").shouldHave(text("test.txt"));
    }

    @Test
    @DisplayName("Скачивание текстового файла и проверка его содержимого")
    void downloadSimpleFileTest() throws IOException {
        open("https://github.com/selenide/selenide/blob/master/README.md");
        File downloadedTxt = $("#raw-url").download();
        String txtContent = IOUtils.toString(new FileReader(downloadedTxt));
        assertTrue(txtContent.contains("Selenide is based on and is compatible to Selenium WebDriver 4.0+"));
    }

    @Test
    @DisplayName("Скачивание PDF файла и проверка кол-ва страниц")
    void pdfFileDownloadTest() throws IOException {
        open("http://earchive.tpu.ru/handle/11683/66318");
        File pdf = $(byText("Просмотреть/Открыть")).download();
        PDF parsedPdf = new PDF(pdf);
        assertEquals(86, parsedPdf.numberOfPages);
    }

    @Test
    @DisplayName("Скачивание XLS файла и проверка содержания текста в определенной ячейке")
    void xlsFileDownloadTest() throws IOException {
        open("https://file-examples.com/index.php/sample-documents-download/sample-xls-download/");
        File xls = $$("a[href*='file_example_XLS_10']")
                .find(text("Download sample xls file"))
                .download();

        XLS parsedXls = new XLS(xls);
        boolean checkCell = parsedXls.excel
                .getSheetAt(0)
                .getRow(1)
                .getCell(1)
                .getStringCellValue()
                .contains("Dulce");

        assertTrue(checkCell);
    }

    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("test.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> strings = csvReader.readAll();
            assertEquals(3, strings.size());
        }
    }

    @Test
    @DisplayName("Парсинг и проверка названия файла в ZIP архиве")
    void checkingFileNameInZipArchiveTest() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                assertEquals("test.csv", entry.getName());
            }
        }
    }
}