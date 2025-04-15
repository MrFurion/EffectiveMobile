package by.effective.mobile.eb.enums;

public enum Roles {
    ADMIN("ROLE_ADMIN"),
    VIEWER("ROLE_USER");

    private final String roleName;
    Roles(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleName(){
        return roleName;
    }
}
