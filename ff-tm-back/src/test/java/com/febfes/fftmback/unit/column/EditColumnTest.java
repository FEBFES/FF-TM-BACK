package com.febfes.fftmback.unit.column;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.repository.ColumnRepository;
import com.febfes.fftmback.service.impl.ColumnServiceImpl;
import com.febfes.fftmback.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditColumnTest {

    private static final Long FIRST_ID = 1L;
    private static final String COLUMN_NAME = "Test Column";
    private static final String COLUMN_NAME_2 = "New Test Column";

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
    void testEditColumn() {
        // Create test column
        TaskColumnEntity column = new TaskColumnEntity();
        column.setId(FIRST_ID);
        column.setName(COLUMN_NAME);
        column.setProjectId(FIRST_ID);

        // Mock the column repository
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        when(orderService.getNewOrder(any())).thenReturn(FIRST_ID.intValue());

        // Call the editColumn method
        ColumnDto columnDto = new ColumnDto(column.getId(), COLUMN_NAME_2, column.getCreateDate(), FIRST_ID.intValue(), FIRST_ID);
        TaskColumnEntity updatedColumn = columnService.editColumn(columnDto, FIRST_ID);

        // Verify that the column was updated
        assertEquals(COLUMN_NAME_2, updatedColumn.getName());

        verify(orderService).editOrder(any(), eq(FIRST_ID.intValue()));
    }
}
