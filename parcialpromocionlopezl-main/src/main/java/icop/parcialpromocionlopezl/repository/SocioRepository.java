package icop.parcialpromocionlopezl.repository;
import icop.parcialpromocionlopezl.entity.socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository 
public interface SocioRepository extends JpaRepository<socio, Long>{
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Long id);
    Optional<socio> findByDni(String dni);
    List<socio> findByApellido(String apellido);
}

