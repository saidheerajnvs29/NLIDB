package org.ncr.nlidb.repository;

import org.ncr.nlidb.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long>,ProductRepositoryCustom{

}
