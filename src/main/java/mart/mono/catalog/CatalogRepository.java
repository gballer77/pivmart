package mart.mono.catalog;

import mart.mono.catalog.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, UUID> {

}