package org.ncr.nlidb.repository;


import org.ncr.nlidb.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,CustomerRepositoryCustom 
{
	
}
