package com.api.freelancer.freelancer;

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
public interface FreelancerApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "View a list of available freelancers", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved freelancers list"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<List<FreelancerResponseDto>> getAllFreelancers() ;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retrieve a freelancer by id", response = FreelancerResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved freelancer"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<FreelancerResponseDto> getFreelancerById(@PathParam("id") @ApiParam("id of the freelancer") Long id) ;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add a freelancer", response = FreelancerResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a freelancer"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<FreelancerResponseDto> addFreelancer(@ApiParam(value = "request json",
                                                  required = true)
                                                  FreelancerRequestDto freelancerRequestDto);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a freelancer", response = FreelancerResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a freelancer"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<FreelancerResponseDto> updateFreelancer(@PathParam("id") @ApiParam("id of the freelancer") Long id,
                                                           @ApiParam(value = "request json", required = true)
                                                           FreelancerRequestDto freelancerRequestDto);

}
