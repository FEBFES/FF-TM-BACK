package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import com.febfes.fftmback.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteColumnTest {

    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long THIRD_ID = 3L;
    private static final String COLUMN_NAME = "Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private OrderService<TaskColumnEntity> orderService;

    @InjectMocks
    private ColumnServiceImpl columnService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteColumn() {
        // Create a mock column entity
        TaskColumnEntity columnEntity = new TaskColumnEntity();
        columnEntity.setId(FIRST_ID);
        columnEntity.setName(COLUMN_NAME);
        columnEntity.setProjectId(SECOND_ID);

        when(columnRepository.findById(FIRST_ID)).thenReturn(Optional.of(columnEntity));

        // Call the deleteColumn method
        columnService.deleteColumn(FIRST_ID, 1L);

        // Verify that the column repository was called with the correct parameters
//        verify(columnRepository).updateChildColumn(THIRD_ID, FIRST_ID, SECOND_ID);
        verify(columnRepository).delete(columnEntity);
    }

    @Test
    void testDeleteColumnNotFound() {
        // Mock the column repository to return an empty optional
        when(columnRepository.findById(FIRST_ID)).thenReturn(Optional.empty());

        // Call the deleteColumn method with a non-existent column ID
        assertThrows(EntityNotFoundException.class, () -> columnService.deleteColumn(FIRST_ID, 1L));

        // Verify that the column repository was not called
//        verify(columnRepository, never()).updateChildColumn(anyLong(), anyLong(), anyLong());
        verify(columnRepository, never()).delete(any(TaskColumnEntity.class));
    }
}
