package com.api.freelancer.controller;

import com.api.freelancer.service.FreelancerService;
import com.api.freelancer.freelancer.FreelancerApi;
import com.api.freelancer.freelancer.FreelancerRequestDto;
import com.api.freelancer.freelancer.FreelancerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController implements FreelancerApi {

    @Autowired
    FreelancerService freelancerService;

    @GetMapping
    @Override
    public ResponseEntity<List<FreelancerResponseDto>> getAllFreelancers() {
        return ResponseEntity.ok(freelancerService.findAll());

    }

    @PostMapping
    @Override
    public ResponseEntity<FreelancerResponseDto> addFreelancer(@RequestBody FreelancerRequestDto freelancerRequestDto) {
        return ResponseEntity.ok(freelancerService.save(freelancerRequestDto));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<FreelancerResponseDto> getFreelancerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(freelancerService.findUserById(id));
    }


    @PutMapping("/{id}")
    @Override
    public ResponseEntity<FreelancerResponseDto> updateFreelancer(@PathVariable("id") Long id,
                                                                  @RequestBody FreelancerRequestDto freelancerRequestDto) {
        return ResponseEntity.ok(freelancerService.updateUser(id, freelancerRequestDto));
    }

}
