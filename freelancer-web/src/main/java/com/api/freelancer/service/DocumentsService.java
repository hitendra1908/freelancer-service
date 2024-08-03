package com.api.freelancer.service;

import com.api.freelancer.model.Documents;
import com.api.freelancer.repository.DocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentsService {

    @Autowired
    DocumentsRepository documentsRepository;

    public Optional<Documents> findById(Long id) {
        return documentsRepository.findById(id);
    }

    public Documents save(Documents documents) {
        return documentsRepository.save(documents);
    }
}
