package Panaca.controllers;

import Panaca.dto.admin.DashboardTotalesDTO;
import Panaca.dto.admin.SerieTemporalDTO;
import Panaca.dto.admin.TopEventoDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.Donation;
import Panaca.model.documents.Orden;
import Panaca.repository.CuentaRepository;
import Panaca.repository.DonationRepository;
import Panaca.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(
    origins = {
        "http://localhost:4200",
        "https://panaca-front-erick.vercel.app"
    },
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final OrdenRepository ordenRepo;
    private final DonationRepository donationRepo;
    private final CuentaRepository cuentaRepo;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AdminDashboardController(
            OrdenRepository ordenRepo,
            DonationRepository donationRepo,
            CuentaRepository cuentaRepo,
            MongoTemplate mongoTemplate
    ) {
        this.ordenRepo     = ordenRepo;
        this.donationRepo  = donationRepo;
        this.cuentaRepo    = cuentaRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/totales")
    public ResponseEntity<DashboardTotalesDTO> obtenerTotales() {
        long totalOrdenes      = ordenRepo.count();
        double ingresosTotales = ordenRepo.findAll()
                                           .stream()
                                           .mapToDouble(Orden::getTotal)
                                           .sum();
        long totalDonaciones   = donationRepo.count();
        double montoDonaciones = donationRepo.findAll()
                                             .stream()
                                             .mapToDouble(Donation::getTotal)
                                             .sum();
        LocalDateTime ahora    = LocalDateTime.now();
        LocalDateTime haceUnMes= ahora.minusMonths(1);
        long nuevosUsuarios    = cuentaRepo.countByFechaRegistroBetween(haceUnMes, ahora);

        DashboardTotalesDTO dto = new DashboardTotalesDTO(
            totalOrdenes,
            ingresosTotales,
            totalDonaciones,
            (int) montoDonaciones,
            nuevosUsuarios
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ventas-diarias")
    public ResponseEntity<List<SerieTemporalDTO>> ventasDiarias(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        LocalDate d = (desde != null) ? desde : LocalDate.now().minusMonths(1);
        LocalDate h = (hasta != null) ? hasta : LocalDate.now();

        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("fecha").gte(d).lte(h)),
            Aggregation.project()
                       .andExpression("dateToString('%Y-%m-%d', fecha)").as("fecha")
                       .andInclude("total"),
            Aggregation.group("fecha")
                       .sum("total").as("monto")
                       .count().as("count"),
            Aggregation.project("monto", "count")
                       .and("_id").as("fecha"),
            Aggregation.sort(Sort.Direction.ASC, "fecha")
        );

        AggregationResults<SerieTemporalDTO> resultado =
            mongoTemplate.aggregate(agg, Orden.class, SerieTemporalDTO.class);

        return ResponseEntity.ok(resultado.getMappedResults());
    }

    @GetMapping("/donaciones-diarias")
    public ResponseEntity<List<SerieTemporalDTO>> donacionesDiarias(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        LocalDate d = (desde != null) ? desde : LocalDate.now().minusMonths(1);
        LocalDate h = (hasta != null) ? hasta : LocalDate.now();

        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("fecha").gte(d).lte(h)),
            Aggregation.project()
                       .andExpression("dateToString('%Y-%m-%d', fecha)").as("fecha")
                       .andInclude("total"),
            Aggregation.group("fecha")
                       .sum("total").as("monto")
                       .count().as("count"),
            Aggregation.project("monto", "count")
                       .and("_id").as("fecha"),
            Aggregation.sort(Sort.Direction.ASC, "fecha")
        );

        AggregationResults<SerieTemporalDTO> resultado =
            mongoTemplate.aggregate(agg, Donation.class, SerieTemporalDTO.class);

        return ResponseEntity.ok(resultado.getMappedResults());
    }

    @GetMapping("/top-eventos")
    public ResponseEntity<List<TopEventoDTO>> topEventos(
        @RequestParam(defaultValue = "5") int limit
    ) {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.unwind("detalle"),
            Aggregation.group("detalle.idEvento")
                       .sum("detalle.cantidad").as("totalTicketsVendidos"),
            Aggregation.project("totalTicketsVendidos")
                       .and("_id").as("eventoId"),
            Aggregation.sort(Sort.Direction.DESC, "totalTicketsVendidos"),
            Aggregation.limit(limit)
        );

        AggregationResults<TopEventoDTO> resultado =
            mongoTemplate.aggregate(agg, Orden.class, TopEventoDTO.class);

        return ResponseEntity.ok(resultado.getMappedResults());
    }

    @GetMapping("/compras")
    public ResponseEntity<List<Orden>> listarCompras() {
        return ResponseEntity.ok(ordenRepo.findAll());
    }

    @GetMapping("/donaciones")
    public ResponseEntity<List<Donation>> listarDonaciones() {
        return ResponseEntity.ok(donationRepo.findAll());
    }
}

