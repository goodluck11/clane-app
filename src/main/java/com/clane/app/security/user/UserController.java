package com.clane.app.security.user;

import com.clane.app.core.data.ClaneResponse;
import com.clane.app.core.data.ResponseMessages;
import com.clane.app.security.user.dto.CreateUserDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @ApiOperation("For creating a new user")
    public ResponseEntity<ClaneResponse> createUser(@Valid @RequestBody CreateUserDto userDto) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(ResponseMessages.USER_CREATED)
                .status(HttpStatus.CREATED.value())
                .payload(userService.newUser(userDto)).build());
    }

    @GetMapping("/findUserByPhone/{phone}")
    public ResponseEntity<ClaneResponse> findUserByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message("")
                .status(HttpStatus.OK.value())
                .payload(userService.findByEmailOrPhoneNumber(phone)).build());
    }
}
