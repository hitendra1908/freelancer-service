package com.api.freelancer.repository;

import com.api.freelancer.model.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentsRepository extends JpaRepository<Documents, Long> {

    List<Documents> findByUserUserName(String userName);
}
