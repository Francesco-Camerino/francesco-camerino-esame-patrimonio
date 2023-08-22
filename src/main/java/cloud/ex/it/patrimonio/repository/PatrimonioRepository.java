package cloud.ex.it.patrimonio.repository;

import cloud.ex.it.patrimonio.model.Patrimonio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatrimonioRepository extends JpaRepository<Patrimonio, Long> {

}