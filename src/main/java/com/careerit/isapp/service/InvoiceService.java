package com.careerit.isapp.service;

import com.careerit.isapp.domain.Account;
import com.careerit.isapp.domain.InvoiceItems;
import com.careerit.isapp.dto.AccountDto;
import com.careerit.isapp.dto.Currency;
import com.careerit.isapp.dto.InvoiceDto;
import com.careerit.isapp.dto.InvoiceItemDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceService {

    private List<Account> accounts;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("sample-invoice.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            JsonNode accountsNode = rootNode.get("accounts");
            
            accounts = new ArrayList<>();
            if (accountsNode != null && accountsNode.isArray()) {
                for (JsonNode accountNode : accountsNode) {
                    Account account = objectMapper.treeToValue(accountNode, Account.class);
                    accounts.add(account);
                }
            }
            log.info("Loaded {} accounts from sample-invoice.json", accounts.size());
        } catch (IOException e) {
            log.error("Error loading accounts from sample-invoice.json: {}", e.getMessage(), e);
            accounts = new ArrayList<>();
        }
    }

    public List<InvoiceDto> getInvoices() {
        return accounts.stream()
                .map(this::mapToInvoiceDto)
                .collect(Collectors.toList());
    }

    private InvoiceDto mapToInvoiceDto(Account account) {
        InvoiceDto invoiceDto = new InvoiceDto();
        
        // Map Account to AccountDto
        AccountDto accountDto = new AccountDto();
        accountDto.setName(account.getName());
        accountDto.setCity(account.getCity());
        accountDto.setCountry(account.getCountry());
        
        // Get currency details from external API
        Currency currency = getCurrency(account.getCurrencySymbol());
        accountDto.setCurrency(currency);
        
        invoiceDto.setAccountDto(accountDto);
        
        // Map Invoice Items
        if (account.getInvoice() != null && account.getInvoice().getInvoiceItems() != null) {
            List<InvoiceItemDto> itemDtos = account.getInvoice().getInvoiceItems().stream()
                    .map(item -> mapToInvoiceItemDto(item, currency))
                    .collect(Collectors.toList());
            invoiceDto.setInvoiceItemsDtos(itemDtos);
            
            // Map and format amounts using currency decimal digits
            invoiceDto.setTotalAmount(formatAmount(account.getInvoice().getTotalAmount(), currency));
            invoiceDto.setTaxAmount(formatAmount(account.getInvoice().getTaxAmount(), currency));
            invoiceDto.setNetAmount(formatAmount(account.getInvoice().getNetAmount(), currency));
        }
        
        return invoiceDto;
    }

    private InvoiceItemDto mapToInvoiceItemDto(InvoiceItems item, Currency currency) {
        InvoiceItemDto dto = new InvoiceItemDto();
        dto.setServiceCode(item.getServiceCode());
        dto.setServiceName(item.getServiceName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(formatAmount(item.getQuantity(), currency));
        dto.setAmount(formatAmount(item.getAmount(), currency));
        return dto;
    }

    private BigDecimal formatAmount(double value, Currency currency) {
        BigDecimal amount = BigDecimal.valueOf(value);
        int scale = currency.getDecimalDigits();
        RoundingMode roundingMode = getRoundingMode(currency.getRounding());
        return amount.setScale(scale, roundingMode);
    }

    private RoundingMode getRoundingMode(int rounding) {
        // Common rounding values interpretation:
        // 0 = HALF_UP (default), 1 = UP, -1 = DOWN, etc.
        return switch (rounding) {
            case 1 -> RoundingMode.UP;
            case -1 -> RoundingMode.DOWN;
            case 2 -> RoundingMode.CEILING;
            case -2 -> RoundingMode.FLOOR;
            default -> RoundingMode.HALF_UP;
        };
    }

    private Currency getCurrency(String currencyCode) {
        try {
            String url = "https://currency-service-app.onrender.com/api/v1/currency/" + currencyCode;
            Currency currency = restTemplate.getForObject(url, Currency.class);
            if (currency != null) {
                return currency;
            }
        } catch (Exception e) {
            log.error("Error fetching currency details for {}: {}", currencyCode, e.getMessage());
        }
        throw new IllegalArgumentException("Currency code not found");
    }

    // TODO add method to download all the invoices as zip file which should contains all the invoices (pdf)

}
