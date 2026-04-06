package com.finance.finance_api.service;

import com.finance.finance_api.dto.RecordDTO;
import com.finance.finance_api.model.FinancialRecord;
import com.finance.finance_api.model.TransactionType;
import com.finance.finance_api.model.User;
import com.finance.finance_api.repository.FinancialRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private FinancialRecordRepository recordRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RecordService recordService;

    private User user;
    private FinancialRecord record;
    private RecordDTO recordDTO;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testuser").build();
        record = FinancialRecord.builder()
                .id(1L)
                .amount(java.math.BigDecimal.valueOf(100.0))
                .type(TransactionType.INCOME)
                .user(user)
                .build();
        recordDTO = new RecordDTO();
        recordDTO.setAmount(java.math.BigDecimal.valueOf(100.0));
        recordDTO.setType(TransactionType.INCOME);
    }

    @Test
    void createRecord_ShouldReturnSavedRecordDTO() {
        // Arrange
        when(modelMapper.map(any(RecordDTO.class), eq(FinancialRecord.class))).thenReturn(record);
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);
        when(modelMapper.map(any(FinancialRecord.class), eq(RecordDTO.class))).thenReturn(recordDTO);

        // Act
        RecordDTO result = recordService.createRecord(user, recordDTO);

        // Assert
        assertNotNull(result);
        assertEquals(java.math.BigDecimal.valueOf(100.0), result.getAmount());
        verify(recordRepository).save(any(FinancialRecord.class));
    }

    @Test
    void updateRecord_ShouldThrowException_WhenUserNotOwner() {
        // Arrange
        User anotherUser = User.builder().id(2L).build();
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            recordService.updateRecord(anotherUser, 1L, recordDTO)
        );
    }

    @Test
    void deleteRecord_ShouldCallDelete_WhenOwner() {
        // Arrange
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));

        // Act
        recordService.deleteRecord(user, 1L);

        // Assert
        verify(recordRepository).delete(record);
    }

    @Test
    void deleteRecord_ShouldThrowException_WhenRecordNotFound() {
        // Arrange
        when(recordRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            recordService.deleteRecord(user, 1L)
        );
    }
}
