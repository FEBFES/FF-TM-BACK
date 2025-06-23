package com.febfes.fftmback.unit.file;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteFileTest {

    private static final Long FIRST_ID = 1L;
    private static final String FILE_URN = "test-file-urn";

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteFileById() {
        // Arrange
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(FIRST_ID);
        fileEntity.setFileUrn(FILE_URN);
        fileEntity.setEntityType(EntityType.USER_PIC);
        fileEntity.setFilePath("/path/to/file");
        when(fileRepository.findById(FIRST_ID)).thenReturn(Optional.of(fileEntity));
        // Act & Assert
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);

            fileService.deleteFileById(FIRST_ID);

            verify(fileRepository).deleteById(FIRST_ID);
            mockedFiles.verify(() -> Files.deleteIfExists(Path.of(fileEntity.getFilePath())));
        }
    }
}
