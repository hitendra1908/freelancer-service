package com.api.freelancer.document;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/freelancer")
public interface DocumentApi {

    @GET
    @Path("/documents/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Fetch a document by its id", response = DocumentDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Document"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentDto> getDocument(Long id) ;

    @POST
    @Path("/documents")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Add a Document", response = DocumentDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully uploaded a Document"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentDto> uploadDocument(DocumentRequestDto documentRequest, MultipartFile file);

}
