package com.api.freelancer.document;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/freelancer")
public interface DocumentApi {

    @POST
    @Path("/documents")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Add a Document", response = DocumentResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully uploaded a Document"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 413, message = "File is too large"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    ResponseEntity<DocumentResponseDto> uploadDocument(DocumentRequestDto documentRequest, MultipartFile file);

}
