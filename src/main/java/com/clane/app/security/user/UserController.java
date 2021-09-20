package com.clane.app.security.user;

import com.clane.app.core.data.ClaneResponse;
import com.clane.app.core.data.ResponseMessages;
import com.clane.app.security.user.dto.CreateUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Api(value="Api for creating and managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @ApiOperation(value = "For creating a new user", response = ClaneResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = ResponseMessages.USER_CREATED),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to access is not found")
    })
    public ResponseEntity<ClaneResponse> createUser(@Valid @RequestBody CreateUserDto userDto) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(ResponseMessages.USER_CREATED)
                .status(HttpStatus.CREATED.value())
                .payload(userService.newUser(userDto)).build());
    }

    @GetMapping("/findUserByPhoneOrEmail/{ref}")
    @ApiOperation(value = "For testing that the cache works", response = ClaneResponse.class, consumes = "string", produces = "application/json")
    public ResponseEntity<ClaneResponse> findUserByPhone(@PathVariable String ref) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message("")
                .status(HttpStatus.OK.value())
                .payload(userService.findByEmailOrPhoneNumber(ref)).build());
    }
}
