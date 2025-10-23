package org.frogcy.furnitureadmin.shippingfee.impl;

import org.frogcy.furnitureadmin.shippingfee.*;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeMapper;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeRequestDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeResponseDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeUpdateDTO;
import org.frogcy.furnitureadmin.user.UserRepository;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.ShippingFee;
import org.frogcy.furniturecommon.entity.User;
import org.frogcy.furniturecommon.entity.address.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShippingFeeServiceImpl implements ShippingFeeService {
    private final ShippingFeeRepository repository;
    private final ProvinceRepository provinceRepository;
    private final ShippingFeeMapper shippingFeeMapper;
    private final ShippingFeeRepository shippingFeeRepository;
    private final UserRepository userRepository;

    public ShippingFeeServiceImpl(ShippingFeeRepository repository, ProvinceRepository provinceRepository, ShippingFeeMapper shippingFeeMapper, ShippingFeeRepository shippingFeeRepository, UserRepository userRepository) {
        this.repository = repository;
        this.provinceRepository = provinceRepository;
        this.shippingFeeMapper = shippingFeeMapper;
        this.shippingFeeRepository = shippingFeeRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ShippingFeeResponseDTO create(ShippingFeeRequestDTO dto) {
        Province province = provinceRepository.findByCode(dto.getProvinceCode())
                .orElseThrow(() -> new ProvinceNotFoundException("Province not found"));

        if(shippingFeeRepository.findByProvinceCodeAndDeletedIsFalse(province.getCode()).isPresent()){
            throw new ShippingFeeAlreadyExist("Shipping fee already exists with province code: " + province.getCode());
        }

        ShippingFee shippingFee = shippingFeeMapper.toEntity(dto);
        shippingFee.setProvince(province);

        ShippingFee saved = repository.save(shippingFee);
        ShippingFeeResponseDTO response = shippingFeeMapper.toDto(saved);
        response.setProvinceCode(province.getCode());
        return response;
    }

    @Override
    public ShippingFeeResponseDTO update(Integer id, ShippingFeeUpdateDTO dto) {
        ShippingFee fee = shippingFeeRepository.findById(id)
                .orElseThrow(() -> new ShippingFeeNotFoundException("Shipping fee not found"));

        shippingFeeMapper.updateEntityFromDto(dto, fee);
        shippingFeeRepository.save(fee);

        ShippingFeeResponseDTO response = shippingFeeMapper.toDto(fee);
        response.setProvinceCode(fee.getProvince().getCode());
        return response;
    }

    @Override
    public void delete(Integer id, User user) {
        ShippingFee fee = shippingFeeRepository.findById(id)
                .orElseThrow(() -> new ShippingFeeNotFoundException("Shipping fee not found"));

        fee.setDeleted(true);
        fee.setDeletedAt(new Date());
        fee.setDeletedById(user.getId());

        shippingFeeRepository.save(fee);
    }

    @Override
    public PageResponseDTO<ShippingFeeResponseDTO> getAllShippingFees(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShippingFee> shippingFees = shippingFeeRepository.search(keyword, pageable);

        List<ShippingFeeResponseDTO> response = shippingFees.getContent().stream()
                .map(shippingFee -> {
                    ShippingFeeResponseDTO responseDTO = shippingFeeMapper.toDto(shippingFee);
                    responseDTO.setProvinceCode(shippingFee.getProvince().getCode());
                    responseDTO.setProvinceName(shippingFee.getProvince().getName());
                    return responseDTO;
                }).toList();

        return new PageResponseDTO<>(response,
                shippingFees.getNumber(),
                shippingFees.getSize(),
                shippingFees.getTotalElements(),
                shippingFees.getTotalPages());
    }
}
