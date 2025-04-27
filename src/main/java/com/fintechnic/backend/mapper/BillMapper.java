package com.fintechnic.backend.mapper;

import com.fintechnic.backend.dto.response.BillResponseDTO;
import com.fintechnic.backend.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(source = "type", target = "type")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "isPaid", target = "isPaid")
    BillResponseDTO billToBillResponseDTO(Bill bill);
}
