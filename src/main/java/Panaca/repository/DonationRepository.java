package Panaca.repository;

import Panaca.model.documents.Donation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DonationRepository extends MongoRepository<Donation, String> {
    List<Donation> findByIdDonanteOrderByFechaDesc(String idDonante);

}
