package com.febfes.fftmback.unit.file;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.impl.FileServiceImpl;
import com.febfes.fftmback.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetFileTest {

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

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFile() {
        // Arrange
        FileEntity file = new FileEntity();
        file.setFileUrn(FILE_URN);

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
        FileEntity fileEntity1 = new FileEntity();
        fileEntity1.setEntityId(FIRST_ID);
        fileEntity1.setEntityType(EntityType.TASK);
        FileEntity fileEntity2 = new FileEntity();
        fileEntity2.setEntityId(FIRST_ID);
        fileEntity2.setEntityType(EntityType.TASK);

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
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(FIRST_ID);
        fileEntity.setFileUrn(USER_FILE_URN);
        fileEntity.setFilePath(FILE_PATH);
        fileEntity.setEntityType(EntityType.USER_PIC);

        when(fileRepository.findByFileUrn(USER_FILE_URN)).thenReturn(Optional.of(fileEntity));
        when(fileRepository.findByFileUrn(TASK_FILE_URN)).thenReturn(Optional.of(fileEntity));
        File mockedUserPicFile = mock(File.class);
        Path mockedUserPicPath = mock(Path.class);
        when(mockedUserPicFile.toPath()).thenReturn(mockedUserPicPath);
        when(mockedUserPicPath.toString()).thenReturn(FILE_PATH);

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
