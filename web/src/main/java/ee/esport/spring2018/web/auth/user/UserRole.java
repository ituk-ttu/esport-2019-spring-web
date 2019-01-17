package ee.esport.spring2018.web.auth.user;

public enum UserRole {

    USER,
    ADMIN;

    public boolean isAtleast(UserRole other) {
        return other.ordinal() <= this.ordinal();
    }

}
