package com.finance.finance_api.service;

import com.finance.finance_api.dto.RecordDTO;
import com.finance.finance_api.model.FinancialRecord;
import com.finance.finance_api.model.TransactionType;
import com.finance.finance_api.model.User;
import com.finance.finance_api.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final FinancialRecordRepository recordRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<RecordDTO> getAllRecords(User user) {
        return recordRepository.findByUserId(user.getId())
                .stream()
                .map(record -> modelMapper.map(record, RecordDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecordDTO> filterRecords(User user, String category, TransactionType type, LocalDate startDate, LocalDate endDate) {
        return recordRepository.filterRecords(user.getId(), category, type, startDate, endDate)
                .stream()
                .map(record -> modelMapper.map(record, RecordDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public RecordDTO createRecord(User user, RecordDTO recordDTO) {
        FinancialRecord record = modelMapper.map(recordDTO, FinancialRecord.class);
        record.setUser(user);
        FinancialRecord savedRecord = recordRepository.save(record);
        return modelMapper.map(savedRecord, RecordDTO.class);
    }

    @Transactional
    public RecordDTO updateRecord(User user, Long recordId, RecordDTO recordDTO) {
        FinancialRecord existingRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (!existingRecord.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to record");
        }

        existingRecord.setAmount(recordDTO.getAmount());
        existingRecord.setType(recordDTO.getType());
        existingRecord.setCategory(recordDTO.getCategory());
        existingRecord.setDate(recordDTO.getDate());
        existingRecord.setDescription(recordDTO.getDescription());

        FinancialRecord updatedRecord = recordRepository.save(existingRecord);
        return modelMapper.map(updatedRecord, RecordDTO.class);
    }

    @Transactional
    public void deleteRecord(User user, Long recordId) {
        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to record");
        }

        recordRepository.delete(record);
    }
}
