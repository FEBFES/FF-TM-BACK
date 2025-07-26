package com.febfes.fftmback.unit.file;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.repository.FileRepository;
import com.febfes.fftmback.service.impl.FileServiceImpl;
import com.febfes.fftmback.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.febfes.fftmback.util.UnitTestBuilders.file;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteFileTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final String FILE_URN = "test-file-urn";

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;


    @Test
    void testDeleteFileById() {
        // Arrange
        FileEntity fileEntity = file(FIRST_ID, FILE_URN, EntityType.USER_PIC, "/path/to/file");
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
