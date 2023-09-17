package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EditColumnTest {

    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final String COLUMN_NAME = "Test Column";
    private static final String COLUMN_NAME_2 = "New Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @InjectMocks
    private ColumnServiceImpl columnService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEditColumn() {
        // Create test columns
        TaskColumnEntity column = new TaskColumnEntity();
        column.setId(FIRST_ID);
        column.setName(COLUMN_NAME);
        column.setProjectId(FIRST_ID);

        // Mock the column repository
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        // Call the editColumn method
        ColumnDto columnDto = new ColumnDto(column.getId(), COLUMN_NAME_2, column.getCreateDate(), 2, FIRST_ID);
        TaskColumnEntity updatedColumn = columnService.editColumn(columnDto, FIRST_ID, FIRST_ID);

        // Verify that the column was updated
        assertEquals(COLUMN_NAME_2, updatedColumn.getName());
//        assertNull(updatedColumn.getChildTaskColumnId());
    }
}
