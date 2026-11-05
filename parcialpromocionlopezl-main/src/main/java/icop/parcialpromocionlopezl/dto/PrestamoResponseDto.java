package icop.parcialpromocionlopezl.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
@Getter 
public class PrestamoResponseDto {
    private Long id;
    private String tituloLibro;
    private LocalDate fechaPrestamo;
    private boolean devuelto;
    private String nombreSocio;
}
