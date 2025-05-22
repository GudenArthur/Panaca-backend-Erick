package Panaca.model.documents;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("donaciones")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Donation {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String idDonante;
    private LocalDateTime fecha;
    private int total;

    private List<DonationItem> items;
}