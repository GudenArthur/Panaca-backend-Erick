db = connect('mongodb://localhost:27017/panaca-test');

db.cuentas.insertMany([
    {
        _id: ObjectId("66a2a9aaa8620e3c1c5437be"),
        cedula: "1234567890",
        nombre: "Pepito Perez",
        telefono: "3001234567",
        email: "pepito@test.com",
        password: "encryptedpass1",
        codigoVerificacionRegistro: "12345",
        codigoVerificacionContrasenia: null,
        fechaExpiracionCodigo: new Date(),
        fechaExpiracionCodigoContrasenia: null,
        fechaRegistro: new Date(),
        rol: "CLIENTE",
        estado: "ACTIVO",
        _class: "Panaca.model.documents.Cuenta"
    },
    {
        _id: ObjectId("66a2a9aaa8620e3c1c5437bf"),
        cedula: "9876543210",
        nombre: "Maria Gomez",
        telefono: "3107654321",
        email: "maria@test.com",
        password: "encryptedpass2",
        codigoVerificacionRegistro: "67890",
        codigoVerificacionContrasenia: null,
        fechaExpiracionCodigo: new Date("2020-01-01T00:00:00Z"),
        fechaExpiracionCodigoContrasenia: null,
        fechaRegistro: new Date(),
        rol: "CLIENTE",
        estado: "INACTIVO",
        _class: "Panaca.model.documents.Cuenta"
    },
    {
        _id: ObjectId("66a2a9aaa8620e3c1c5437c0"),
        cedula: "1112223334",
        nombre: "Admin User",
        telefono: "3200000000",
        email: "admin@test.com",
        password: "encryptedadmin",
        codigoVerificacionRegistro: "54321",
        codigoVerificacionContrasenia: null,
        fechaExpiracionCodigo: new Date(),
        fechaExpiracionCodigoContrasenia: null,
        fechaRegistro: new Date(),
        rol: "ADMINISTRADOR",
        estado: "ACTIVO",
        _class: "Panaca.model.documents.Cuenta"
    }
]);

db.carrito.insertOne({
    _id: ObjectId(),
    idUsuario: "66a2a9aaa8620e3c1c5437be",
    items: [],
    _class: "Panaca.model.documents.Carrito"
});
