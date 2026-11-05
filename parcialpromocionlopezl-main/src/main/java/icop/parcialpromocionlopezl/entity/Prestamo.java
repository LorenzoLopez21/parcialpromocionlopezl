package icop.parcialpromocionlopezl.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Entity 
@Table(name = "Prestamo")
@Getter
@Setter
public class Prestamo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Column(name = "titulo_libro")
    private String tituloLibro;
    @Column(name = "fecha_prestamo")
    private LocalDate fechaPrestamo;
    private boolean devuelto;
    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private socio socio;
    
}
