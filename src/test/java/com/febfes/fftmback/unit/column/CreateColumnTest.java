package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateColumnTest {

    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final String COLUMN_NAME = "Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @InjectMocks
    private ColumnServiceImpl columnService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateColumn() {
        // Create a mock TaskColumnEntity object
        TaskColumnEntity column = new TaskColumnEntity();
        column.setName(COLUMN_NAME);
        column.setProjectId(FIRST_ID);

        // Mock the columnRepository to return the saved column
        when(columnRepository.save(column)).thenReturn(column);

        // Call the createColumn method
        TaskColumnEntity savedColumn = columnService.createColumn(column, FIRST_ID);

        // Verify that the columnRepository was called with the correct argument
        verify(columnRepository).save(column);

        // Verify that the columnRepository was called to update the child column
//        verify(columnRepository).updateChildColumn(
//                savedColumn.getId(),
//                savedColumn.getChildTaskColumnId(),
//                savedColumn.getProjectId()
//        );

        // Verify that the saved column is returned
        assertEquals(column, savedColumn);
    }

    @Test
    void testCreateColumnThrowsException() {
        // Create a mock TaskColumnEntity object
        TaskColumnEntity column = new TaskColumnEntity();
        column.setName(COLUMN_NAME);
        column.setProjectId(FIRST_ID);

        // Mock the columnRepository to throw an exception
        given(columnRepository.save(column)).willAnswer(invocation -> {
            throw new Exception("Test exception");
        });

        // Call the createColumn method
        assertThrows(Exception.class, () -> columnService.createColumn(column, SECOND_ID));

        // Verify that the columnRepository was called with the correct argument
        verify(columnRepository).save(column);

        // Verify that the columnRepository was not called to update the child column
//        verify(columnRepository, never()).updateChildColumn(
//                anyLong(),
//                anyLong(),
//                anyLong()
//        );
    }
}
