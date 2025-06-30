package com.febfes.fftmback.unit.file;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.impl.FileServiceImpl;
import com.febfes.fftmback.unit.BaseUnitTest;
import com.febfes.fftmback.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.febfes.fftmback.util.UnitTestBuilders.file;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetFileTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final String FILE_URN = "test-file-urn";
    private static final String ID_FOR_URN = "123";
    private static final String FILE_PATH = "test-file-path";
    private static final String USER_FILE_URN = String.format(FileUtils.USER_PIC_URN, Long.parseLong(ID_FOR_URN));
    private static final String TASK_FILE_URN = String.format(FileUtils.TASK_FILE_URN, Long.parseLong(ID_FOR_URN));
    private static final byte[] EXPECTED_BYTES = "test content".getBytes();

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;


    @Test
    void testGetFile() {
        // Arrange
        FileEntity file = file(null, FILE_URN, null, null);

        when(fileRepository.findByFileUrn(FILE_URN)).thenReturn(Optional.of(file));

        // Act
        FileEntity result = fileService.getFile(FILE_URN);

        // Assert
        assertEquals(FILE_URN, result.getFileUrn());
    }

    @Test
    void testGetFileNotFound() {
        // Act
        assertThrows(EntityNotFoundException.class, () -> fileService.getFile(FILE_URN));
    }

    @Test
    void testGetFilesByEntityId() {
        // Arrange
        FileEntity fileEntity1 = file(null, null, EntityType.TASK, null);
        fileEntity1.setEntityId(FIRST_ID);
        FileEntity fileEntity2 = file(null, null, EntityType.TASK, null);
        fileEntity2.setEntityId(FIRST_ID);

        when(fileRepository.findAllByEntityIdAndEntityType(FIRST_ID, EntityType.TASK))
                .thenReturn(Arrays.asList(fileEntity1, fileEntity2));

        // Act
        List<FileEntity> result = fileService.getFilesByEntityId(FIRST_ID, EntityType.TASK);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetFilesByEntityIdNoResults() {
        // Act
        List<FileEntity> result = fileService.getFilesByEntityId(FIRST_ID, EntityType.USER_PIC);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFileContent() throws IOException {
        // Arrange
        FileEntity fileEntity = file(FIRST_ID, USER_FILE_URN, EntityType.USER_PIC, FILE_PATH);

        when(fileRepository.findByFileUrn(USER_FILE_URN)).thenReturn(Optional.of(fileEntity));
        when(fileRepository.findByFileUrn(TASK_FILE_URN)).thenReturn(Optional.of(fileEntity));

        // static method
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(any())).thenReturn(EXPECTED_BYTES);

            // Act
            byte[] userPicResult = fileService.getFileContent(ID_FOR_URN, EntityType.USER_PIC);
            byte[] taskResult = fileService.getFileContent(ID_FOR_URN, EntityType.TASK);

            // Assert
            assertArrayEquals(EXPECTED_BYTES, userPicResult);
            assertArrayEquals(EXPECTED_BYTES, taskResult);

            // Testing with invalid entity type
            assertThrows(IllegalArgumentException.class, () -> fileService.getFileContent(ID_FOR_URN, null));
        }
    }
}
