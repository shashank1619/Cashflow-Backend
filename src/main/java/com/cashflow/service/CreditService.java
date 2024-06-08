package com.cashflow.service;

import com.cashflow.dto.CreditDTO;
import com.cashflow.exception.ResourceNotFoundException;
import com.cashflow.model.Credit;
import com.cashflow.model.User;
import com.cashflow.repository.CreditRepository;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Credit-related business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreditService {

    private final CreditRepository creditRepository;
    private final UserRepository userRepository;

    /**
     * Add a new credit
     */
    public CreditDTO addCredit(CreditDTO creditDTO) {
        User user = userRepository.findById(creditDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creditDTO.getUserId()));

        Credit credit = Credit.builder()
                .amount(creditDTO.getAmount())
                .source(creditDTO.getSource())
                .description(creditDTO.getDescription())
                .creditDate(creditDTO.getCreditDate() != null ? creditDTO.getCreditDate() : LocalDate.now())
                .creditType(creditDTO.getCreditType())
                .isRecurring(creditDTO.getIsRecurring() != null ? creditDTO.getIsRecurring() : false)
                .recurringFrequency(creditDTO.getRecurringFrequency())
                .user(user)
                .build();

        Credit savedCredit = creditRepository.save(credit);
        return mapToDTO(savedCredit);
    }

    /**
     * Get credit by ID
     */
    @Transactional(readOnly = true)
    public CreditDTO getCreditById(Long id) {
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", id));
        return mapToDTO(credit);
    }

    /**
     * Get all credits for a user
     */
    @Transactional(readOnly = true)
    public List<CreditDTO> getCreditsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return creditRepository.findByUserIdOrderByCreditDateDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get total credits for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalCredits(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return creditRepository.getTotalCreditsByUserId(userId);
    }

    /**
     * Get credits by date range
     */
    @Transactional(readOnly = true)
    public List<CreditDTO> getCreditsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return creditRepository.findByUserIdAndCreditDateBetween(userId, startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get credits by source
     */
    @Transactional(readOnly = true)
    public List<CreditDTO> getCreditsBySource(Long userId, String source) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return creditRepository.findByUserIdAndSource(userId, source).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update credit
     */
    public CreditDTO updateCredit(Long id, CreditDTO creditDTO) {
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", id));

        if (creditDTO.getAmount() != null)
            credit.setAmount(creditDTO.getAmount());
        if (creditDTO.getSource() != null)
            credit.setSource(creditDTO.getSource());
        if (creditDTO.getDescription() != null)
            credit.setDescription(creditDTO.getDescription());
        if (creditDTO.getCreditDate() != null)
            credit.setCreditDate(creditDTO.getCreditDate());
        if (creditDTO.getCreditType() != null)
            credit.setCreditType(creditDTO.getCreditType());

        Credit updatedCredit = creditRepository.save(credit);
        return mapToDTO(updatedCredit);
    }

    /**
     * Delete credit
     */
    public void deleteCredit(Long id) {
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit", "id", id));
        creditRepository.delete(credit);
    }

    /**
     * Map Credit entity to DTO
     */
    private CreditDTO mapToDTO(Credit credit) {
        return CreditDTO.builder()
                .id(credit.getId())
                .amount(credit.getAmount())
                .source(credit.getSource())
                .description(credit.getDescription())
                .creditDate(credit.getCreditDate())
                .creditType(credit.getCreditType())
                .isRecurring(credit.getIsRecurring())
                .recurringFrequency(credit.getRecurringFrequency())
                .userId(credit.getUser().getId())
                .username(credit.getUser().getUsername())
                .createdAt(credit.getCreatedAt())
                .updatedAt(credit.getUpdatedAt())
                .build();
    }
}
