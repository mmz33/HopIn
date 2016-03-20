package aub.hopin;

public enum ServerRequestTag {
    SignUp,
    SignIn,
    ChangeProfilePicture,
    ChangePassword,
    ChangeSchedule,
    ChangePhoneNumber,
    ChangeVehicleType,
    ChangeVehiclePassengerCount,
    ChangeStatus,
    ChangeMode,
    QueryMapHistory,
    RateApp,
    ReportProblem,
    GiveFeedback;
    ServerRequestTag[] val = ServerRequestTag.values();
}
