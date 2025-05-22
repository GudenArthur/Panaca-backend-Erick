package Panaca.repository;

import Panaca.model.documents.Donation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DonationItemRepository extends MongoRepository<Donation, String> { }
