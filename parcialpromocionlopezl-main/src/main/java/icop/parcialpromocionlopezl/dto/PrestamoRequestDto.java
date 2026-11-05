package icop.parcialpromocionlopezl.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter 

public class PrestamoRequestDto {
    private String tituloLibro;
    private LocalDate fechaPrestamo;
    private Long socioId;
}
