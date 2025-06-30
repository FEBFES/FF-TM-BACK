package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import com.febfes.fftmback.service.order.OrderService;
import com.febfes.fftmback.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.febfes.fftmback.util.UnitTestBuilders.column;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteColumnTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final String COLUMN_NAME = "Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private OrderService<TaskColumnEntity> orderService;

    @InjectMocks
    private ColumnServiceImpl columnService;


    @Test
    void testDeleteColumn() {
        TaskColumnEntity columnEntity = column(FIRST_ID, SECOND_ID, COLUMN_NAME);

        when(columnRepository.findById(FIRST_ID)).thenReturn(Optional.of(columnEntity));

        // Call the deleteColumn method
        columnService.deleteColumn(FIRST_ID);

        // Verify that the column repository was called with the correct parameters
        verify(columnRepository).delete(columnEntity);

        verify(orderService).removeEntity(any());
    }

    @Test
    void testDeleteColumnNotFound() {
        // Mock the column repository to return an empty optional
        when(columnRepository.findById(FIRST_ID)).thenReturn(Optional.empty());

        // Call the deleteColumn method with a non-existent column ID
        assertThrows(EntityNotFoundException.class, () -> columnService.deleteColumn(FIRST_ID));

        // Verify that the column repository was not called
        verify(columnRepository, never()).delete(any(TaskColumnEntity.class));

        verify(orderService, never()).removeEntity(any());
    }
}
