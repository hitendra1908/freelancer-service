package com.api.freelancer.repository;

import com.api.freelancer.model.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsRepository extends JpaRepository<Documents, Long> {

}
