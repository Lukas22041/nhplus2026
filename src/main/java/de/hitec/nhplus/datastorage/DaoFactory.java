package de.hitec.nhplus.datastorage;

public class DaoFactory {

    private static DaoFactory instance;

    private DaoFactory() {
    }

    public static synchronized DaoFactory getDaoFactory() {
        if (DaoFactory.instance == null) {
            DaoFactory.instance = new DaoFactory();
        }
        return DaoFactory.instance;
    }

    public TreatmentDao createTreatmentDao() {
        return new TreatmentDao(ConnectionBuilder.getConnection());
    }

    public PatientDao createPatientDao() {
        return new PatientDao(ConnectionBuilder.getConnection());
    }

    public CaregiverDao createCaregiverDao() {
        return new CaregiverDao(ConnectionBuilder.getConnection());
    }

    public RoleDao createRoleDao() {
        return new RoleDao(ConnectionBuilder.getConnection());
    }

    public UserDao createUserDao() {
        return new UserDao(ConnectionBuilder.getConnection());
    }
}
