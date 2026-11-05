package icop.parcialpromocionlopezl.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter 
@AllArgsConstructor 
public class SocioResponseDto {
    private long id;
    private String nombreCompleto;
    private  String dni;
    private Integer cantidadPrestamos;
}
