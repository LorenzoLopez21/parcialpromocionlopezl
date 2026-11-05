package icop.parcialpromocionlopezl.service;
import icop.parcialpromocionlopezl.dto.PrestamoRequestDto;
 import icop.parcialpromocionlopezl.dto.PrestamoResponseDto;
 import icop.parcialpromocionlopezl.entity.Prestamo;
import icop.parcialpromocionlopezl.entity.socio;
import icop.parcialpromocionlopezl.exception.OperacionInvalidaException;
import icop.parcialpromocionlopezl.exception.RecursoNoEncontradoException;
import icop.parcialpromocionlopezl.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class PrestamoService {
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;
    private final PrestamoRepository prestamoRepository;
    private final SocioService socioService;
    public PrestamoService(PrestamoRepository prestamoRepository, SocioService socioService) {
        this.prestamoRepository = prestamoRepository;
        this.socioService = socioService;
    }
    public PrestamoResponseDto crearPrestamo(PrestamoRequestDto dto) {
        socio socio = socioService.buscarSocioPorId(dto.getSocioId());
        validarTitulo(dto.getTituloLibro());
        if (prestamoRepository.existsByTituloLibroAndDevueltoFalse(dto.getTituloLibro())) {
            throw new OperacionInvalidaException("El libro '" + dto.getTituloLibro() + "' ya está prestado.");
        }
        validarLimite(socio);
        Prestamo prestamo = new Prestamo();
        prestamo.setTituloLibro(dto.getTituloLibro());
        prestamo.setFechaPrestamo(dto.getFechaPrestamo() != null ? dto.getFechaPrestamo() : LocalDate.now());
        prestamo.setDevuelto(false);
        prestamo.setSocio(socio);
        Prestamo guardado = prestamoRepository.save(prestamo);
        return convertirAResponse(guardado);
    }
    public List<PrestamoResponseDto> listarPrestamos() {
        return prestamoRepository.findAll().stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }
    public PrestamoResponseDto obtenerPrestamo(Long id) {
        return convertirAResponse(buscarPrestamoPorId(id));
    }
    public List<PrestamoResponseDto> listarPrestamosDeSocio(Long socioId) {
        socio socio = socioService.buscarSocioPorId(socioId);
        return socio.getPrestamos().stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }
    public List<PrestamoResponseDto> listarPendientes() {
        return prestamoRepository.findByDevueltoFalse().stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }
    public List<PrestamoResponseDto> listarPendientesDeSocio(Long socioId) {
        socioService.buscarSocioPorId(socioId);
        return prestamoRepository.pendientesDeSocio(socioId).stream()
            .map(this::convertirAResponse)
            .collect(Collectors.toList());
    }
    public PrestamoResponseDto actualizarPrestamo(Long id, PrestamoRequestDto dto) {
        Prestamo prestamo = buscarPrestamoPorId(id);
        validarTitulo(dto.getTituloLibro());
        if (prestamoRepository.existsByTituloLibroAndDevueltoFalseAndIdNot(dto.getTituloLibro(), id)) {
            throw new OperacionInvalidaException("El libro '" + dto.getTituloLibro() + "' ya está prestado.");
        }
        prestamo.setTituloLibro(dto.getTituloLibro());
        if (dto.getFechaPrestamo() != null) {
            prestamo.setFechaPrestamo(dto.getFechaPrestamo());
        }
        Prestamo guardado = prestamoRepository.save(prestamo);
        return convertirAResponse(guardado);
    }
    public PrestamoResponseDto asignarSocio(Long idPrestamo, Long idSocio) {
        Prestamo prestamo = buscarPrestamoPorId(idPrestamo);
        socio socio = socioService.buscarSocioPorId(idSocio);
        if (prestamo.getDevuelto()) {
            throw new OperacionInvalidaException("No se puede reasignar el préstamo " + idPrestamo + " porque ya fue devuelto.");
        }
        validarLimite(socio);
        prestamo.setSocio(socio);
        Prestamo guardado = prestamoRepository.save(prestamo);
        return convertirAResponse(guardado);
    }
    public PrestamoResponseDto devolverPrestamo(Long id) {
        Prestamo prestamo = buscarPrestamoPorId(id);
        if (prestamo.getDevuelto()) {
            throw new OperacionInvalidaException("El préstamo " + id + " ya fue devuelto.");
        }
        prestamo.setDevuelto(true);
        Prestamo guardado = prestamoRepository.save(prestamo);
        return convertirAResponse(guardado);
    }
    public void eliminarPrestamo(Long id) {
        Prestamo prestamo = buscarPrestamoPorId(id);
        prestamoRepository.delete(prestamo);
    }
    private Prestamo buscarPrestamoPorId(Long id) {
        return prestamoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con id: " + id));
    }
    private void validarLimite(socio socio) {
        if (prestamoRepository.countBySocioIdAndDevueltoFalse(socio.getId()) >= MAX_PRESTAMOS_ACTIVOS) {
            throw new OperacionInvalidaException("El socio con id " + socio.getId() + " ya tiene " + MAX_PRESTAMOS_ACTIVOS + " préstamos sin devolver.");
        }
    }
    private void validarTitulo(String tituloLibro) {
        if (tituloLibro == null || tituloLibro.isBlank()) {
            throw new OperacionInvalidaException("El título del libro no puede estar vacío.");
        }
    }
    private PrestamoResponseDto convertirAResponse(Prestamo p) {
        String nombreSocio = p.getSocio().getNombre() + " " + p.getSocio().getApellido();
        return new PrestamoResponseDto(p.getId(), p.getTituloLibro(), p.getFechaPrestamo(), p.getDevuelto(), p.getSocio().getId(), nombreSocio);
    }
}
