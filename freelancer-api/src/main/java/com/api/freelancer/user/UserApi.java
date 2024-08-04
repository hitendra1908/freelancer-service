package com.api.freelancer.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/freelancer")
public interface UserApi {

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "View a list of available Users", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved users list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<List<UserResponseDto>> getAllUsers() ;

    @POST
    @Path("/users")
    @ApiOperation(value = "Add a User", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<UserResponseDto> addUser(UserRequestDto userRequestDto);

}
