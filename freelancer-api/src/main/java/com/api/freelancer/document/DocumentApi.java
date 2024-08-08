package com.api.freelancer.document;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/freelancers")
public interface DocumentApi {

    @POST
    @Path("/documents")
    @Consumes({MediaType.MULTIPART_FORM_DATA} )
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Upload a file with JSON data",
            notes = "This endpoint allows you to upload a file in pdf and jpeg format along with some JSON data",
            response = DocumentResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully uploaded a Document"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 409, message = "Conflict: Document already exists "),
            @ApiResponse(code = 413, message = "Uploaded file is too large: maximus size allowed is 5MB"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentResponseDto> uploadDocument(@ApiParam(name = "Document details",
                                                                value = "Document details",
                                                                required = true) DocumentRequestDto documentRequest,
                                                       @ApiParam(name = "Document to upload",
                                                               value = "Document to upload",
                                                               required = true) MultipartFile file);

    @PUT
    @Path("/documents/{id}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Upload a updated file with JSON data",
            notes = "This endpoint allows you to upload a file along with some JSON data",
            response = DocumentResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully updated a Document"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 413, message = "Uploaded file is too large: maximus size allowed is 5MB"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentResponseDto> updateDocument(@PathParam("id") @ApiParam("id of the document") Long id,
                                                       @ApiParam(name = "Document details",
                                                               value = "Document details to update",
                                                               required = true) DocumentRequestDto documentRequest,
                                                       @ApiParam(name = "Document to update",
                                                               value = "Document to update",
                                                               required = true,
                                                               allowMultiple = true) MultipartFile file);

    @GET
    @Path("/documents/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Retrieve a document by id", response = DocumentResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully uploaded a Document"),
            @ApiResponse(code = 404, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Document not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentResponseDto> getDocument(@PathParam("id") @ApiParam("id of the document") Long id);

    @DELETE
    @Path("/documents/{id}")
    @ApiOperation(value = "Delete a document by id", response = DocumentResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted a Document"),
            @ApiResponse(code = 404, message = "Document not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<Void> deleteDocument(@PathParam("id") @ApiParam("id of the document") Long id);

}
