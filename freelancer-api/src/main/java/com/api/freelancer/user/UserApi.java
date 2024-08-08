package com.api.freelancer.user;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/freelancers")
public interface UserApi {

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "View a list of available Users", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved users list"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<List<UserResponseDto>> getAllUsers() ;

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retrieve a User by id", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<UserResponseDto> getUserById(@PathParam("id") @ApiParam("id of the user") Long id) ;

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add a User", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a User"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<UserResponseDto> addUser(@ApiParam(value = "request json", required = true) UserRequestDto userRequestDto);

    @PUT
    @Path("/users/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a User", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a User"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<UserResponseDto> updateUser(@PathParam("id") @ApiParam("id of the user") Long id,
                                               @ApiParam(value = "request json", required = true) UserRequestDto userRequestDto);

}
