package edu.ucsb.cs156.example.repositories;

import org.springframework.data.repository.CrudRepository;

import edu.ucsb.cs156.example.entities.UCSBOrganization;

public interface UCSBOrganizationRepository extends CrudRepository<UCSBOrganization, String>{

}
