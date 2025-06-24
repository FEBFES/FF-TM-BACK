package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import com.febfes.fftmback.service.order.OrderService;
import com.febfes.fftmback.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.febfes.fftmback.util.UnitTestBuilders.column;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditColumnTest extends BaseUnitTest {

    private static final Long FIRST_ID = 1L;
    private static final String COLUMN_NAME = "Test Column";
    private static final String COLUMN_NAME_2 = "New Test Column";

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private OrderService<TaskColumnEntity> orderService;

    @InjectMocks
    private ColumnServiceImpl columnService;


    @Test
    void testEditColumn() {
        TaskColumnEntity column = column(FIRST_ID, FIRST_ID, COLUMN_NAME);

        // Mock the column repository
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        // Call the editColumn method
        ColumnDto columnDto = new ColumnDto(column.getId(), COLUMN_NAME_2, column.getCreateDate(), FIRST_ID.intValue(), FIRST_ID);
        TaskColumnEntity updatedColumn = columnService.editColumn(columnDto, FIRST_ID);

        // Verify that the column was updated
        assertEquals(COLUMN_NAME_2, updatedColumn.getName());

        verify(orderService).editOrder(any(), eq(FIRST_ID.intValue()));
    }
}
