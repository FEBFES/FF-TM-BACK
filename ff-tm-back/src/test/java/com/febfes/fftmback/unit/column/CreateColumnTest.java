package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import com.febfes.fftmback.service.order.OrderService;
import com.febfes.fftmback.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.febfes.fftmback.util.UnitTestBuilders.column;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CreateColumnTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final String COLUMN_NAME = "Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private OrderService<TaskColumnEntity> orderService;

    @InjectMocks
    private ColumnServiceImpl columnService;


    @Test
    void testCreateColumn() {
        TaskColumnEntity column = column(null, FIRST_ID, COLUMN_NAME);

        // Mock the columnRepository to return the saved column
        when(columnRepository.save(column)).thenReturn(column);

        when(orderService.getNewOrder(any())).thenReturn(FIRST_ID.intValue());

        // Call the createColumn method
        TaskColumnEntity savedColumn = columnService.createColumn(column);

        // Verify that the columnRepository was called with the correct argument
        verify(columnRepository).save(column);

        // Verify that the orderService was called to get order
        verify(orderService).getNewOrder(any());

        // Verify that the saved column is returned
        column.setEntityOrder(FIRST_ID.intValue());
        assertEquals(column, savedColumn);
    }

    @Test
    void testCreateColumnThrowsException() {
        TaskColumnEntity column = column(null, FIRST_ID, COLUMN_NAME);

        // Mock the columnRepository to throw an exception
        given(columnRepository.save(column)).willAnswer(invocation -> {
            throw new Exception("Test exception");
        });

        // Call the createColumn method
        assertThrows(Exception.class, () -> columnService.createColumn(column));

        // Verify that the columnRepository was called with the correct argument
        verify(columnRepository).save(column);
    }
}
