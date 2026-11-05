package icop.parcialpromocionlopezl.service;
import icop.parcialpromocionlopezl.dto.SocioRequestDto;
import icop.parcialpromocionlopezl.dto.SocioResponseDto;
import icop.parcialpromocionlopezl.entity.socio;
import icop.parcialpromocionlopezl.exception.OperacionInvalidaException;
import icop.parcialpromocionlopezl.exception.RecursoNoEncontradoException;
import icop.parcialpromocionlopezl.repository.PrestamoRepository;
import icop.parcialpromocionlopezl.repository.SocioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class SocioService {
    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;
    
    public SocioService(SocioRepository socioRepository, PrestamoRepository prestamoRepository) {
        this.socioRepository = socioRepository;
        this.prestamoRepository = prestamoRepository;
    }
        public SocioResponseDto crearSocio(SocioRequestDto dto) {
             if (socioRepository.existsByDni(dto.getDni())) {
                throw new OperacionInvalidaException("Ya existe un socio con el DNI: " + dto.getDni());
            }
            socio socio = new socio();
            socio.setNombre(dto.getNombre());
            socio.setApellido(dto.getApellido());
            socio.setDni(dto.getDni());
            
            socio guardado = socioRepository.save(socio);
            return convertirAResponse(guardado);
        }
        public List<SocioResponseDto> listarSocios() {
            return socioRepository.findAll().stream()
            .map(s -> convertirAResponse(s))
            .collect(Collectors.toList());
        }
        public SocioResponseDto obtenerSocio(Long id) {
            return convertirAResponse(buscarSocioPorId(id));
        }
        public SocioResponseDto buscarPorDni(String dni) {
            socio socio = socioRepository.findByDni(dni).orElseThrow(() -> new 
            RecursoNoEncontradoException("Socio no encontrado con DNI: " + dni)); 
            return convertirAResponse(socio);

        }
        public List<SocioResponseDto> buscarPorApellido(String apellido){
            return socioRepository.findByApellido(apellido).stream().map(s -> convertirAResponse(s))
            .collect(Collectors.toList());

        }    
        public boolean tienePrestamos(Long id) {
            buscarSocioPorId(id);
            return prestamoRepository.existsBySocioId(id);
        }
        public SocioResponseDto actualizarSocio(Long id, SocioRequestDto dto){
            socio socio = buscarSocioPorId(id);
            if (socioRepository.existsByDniAndIdNot(dto.getDni(), id)) {
                throw new OperacionInvalidaException("Ya existe otro socio con el DNI: " + dto.getDni());
            }
            socio.setNombre(dto.getNombre());   
            socio.setApellido(dto.getApellido());
            socio.setDni(dto.getDni());
            socio guardado = socioRepository.save(socio);
            return convertirAResponse(guardado);
        } 
        public void eliminarSocio(Long id) {
            socio socio = buscarSocioPorId(id); 
            if (prestamoRepository.existsBySocioId(id)) {
                throw new OperacionInvalidaException("No se puede eliminar el socio con id " + id + " porque tiene préstamos asociados.");
            }
            socioRepository.delete(socio);
        }
        public socio buscarSocioPorId(Long id){
            return socioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado con id: " + id));

        }
        private SocioResponseDto convertirAResponse(socio s) {
            String nombreCompleto = s.getNombre() + " " + s.getApellido();
            return new SocioResponseDto(s.getId(), nombreCompleto, s.getDni(), s.getPrestamos().size());
        }

    }



