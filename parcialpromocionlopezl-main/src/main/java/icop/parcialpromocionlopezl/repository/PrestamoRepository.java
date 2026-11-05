package icop.parcialpromocionlopezl.repository;
import icop.parcialpromocionlopezl.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository

public interface PrestamoRepository extends JpaRepository<Prestamo,Long> {
    boolean existsBySocioId(Long socioId);
    boolean existsByTituloLibroAndDevueltoFalse(String tituloLibro);
    boolean existsByTituloLibroAndDevueltoFalseAndIdNot(String tituloLibro, Long id);
    long countBySocioIdAndDevueltoFalse(Long socioId);
    List<Prestamo> findByDevueltoFalse();
    boolean existsBySocioIdAndDevueltoFalse(Long socioId);
    @Query("SELECT p FROM Prestamo p WHERE p.socio.id = :socioId AND p.devuelto = false")
    List<Prestamo> pendientesDeSocio(@Param("socioId") Long socioId);
}
