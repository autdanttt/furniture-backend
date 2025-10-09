package org.frogcy.furnitureadmin.shippingfee;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeRequestDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeResponseDTO;
import org.frogcy.furnitureadmin.shippingfee.dto.ShippingFeeUpdateDTO;
import org.frogcy.furnitureadmin.user.UserNotFoundException;
import org.frogcy.furnitureadmin.user.UserRepository;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping-fees")
@Valid
public class ShippingFeeController {

    private final ShippingFeeService shippingFeeService;
    private final UserRepository userRepository;

    public ShippingFeeController(ShippingFeeService shippingFeeService, UserRepository userRepository) {
        this.shippingFeeService = shippingFeeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getShippingFees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<ShippingFeeResponseDTO> response = shippingFeeService.getAllShippingFees(page, size, sortField, sortDir, keyword);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createShippingFee(@RequestBody @Valid ShippingFeeRequestDTO dto){
        ShippingFeeResponseDTO response = shippingFeeService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingFee(@PathVariable Integer id,@RequestBody @Valid ShippingFeeUpdateDTO dto){
        ShippingFeeResponseDTO response = shippingFeeService.update(id,dto);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShippingFee(@PathVariable Integer id){
        User user = getUser();
        shippingFeeService.delete(id,user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private User getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

}
