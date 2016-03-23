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
    GiveFeedback,
    QueryUserInfo,
    ConfirmCode;
    public static ServerRequestTag[] val = ServerRequestTag.values();
}
