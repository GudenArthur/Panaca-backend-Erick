package Panaca.model.enums;

public enum AnimalType {
    PERRO("Perro", 50_000),
    GATO("Gato", 45_000),
    CABALLO("Caballo", 100_000),
    VACA("Vaca", 120_000),
    GALLINA("Gallina", 15_000),
    CONEJO("Conejo", 30_000),
    AVESTRUZ("Avestruz", 150_000),
    CERDO("Cerdo", 80_000);

    private final String displayName;
    private final int pricePerBulto;

    AnimalType(String displayName, int pricePerBulto) {
        this.displayName = displayName;
        this.pricePerBulto = pricePerBulto;
    }

    public String getDisplayName() { return displayName; }
    public int getPricePerBulto() { return pricePerBulto; }
}
